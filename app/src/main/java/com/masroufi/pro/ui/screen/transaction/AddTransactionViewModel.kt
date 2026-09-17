package com.masroufi.pro.ui.screen.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.local.entity.AccountEntity
import com.masroufi.pro.data.local.entity.CategoryEntity
import com.masroufi.pro.data.local.entity.TransactionEntity
import com.masroufi.pro.data.model.TransactionType
import com.masroufi.pro.data.repository.AccountRepository
import com.masroufi.pro.data.repository.CategoryRepository
import com.masroufi.pro.data.repository.TransactionRepository
import com.masroufi.pro.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AddTransactionUiState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val note: String = "",
    val selectedDate: Long = System.currentTimeMillis(),
    val selectedCategory: CategoryEntity? = null,
    val selectedAccount: AccountEntity? = null,
    val selectedCurrency: String = "DZD",
    val categories: List<CategoryEntity> = emptyList(),
    val accounts: List<AccountEntity> = emptyList(),
    val isEditing: Boolean = false,
    val calculatorExpression: String = "",
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val transactionRepo: TransactionRepository,
    private val categoryRepo: CategoryRepository,
    private val accountRepo: AccountRepository,
    private val userPrefs: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        val typeStr = savedStateHandle.get<String>("type")
        val type = typeStr?.let {
            try { TransactionType.valueOf(it) } catch (_: Exception) { TransactionType.EXPENSE }
        } ?: TransactionType.EXPENSE

        _uiState.value = _uiState.value.copy(type = type)
        loadData(type)
    }

    private fun loadData(type: TransactionType) {
        viewModelScope.launch {
            // Load categories for this type
            categoryRepo.getCategoriesByType(type).collect { categories ->
                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    selectedCategory = _uiState.value.selectedCategory ?: categories.firstOrNull()
                )
            }
        }
        viewModelScope.launch {
            // Load accounts
            accountRepo.getAllAccounts().collect { accounts ->
                _uiState.value = _uiState.value.copy(
                    accounts = accounts,
                    selectedAccount = _uiState.value.selectedAccount ?: accounts.firstOrNull()
                )
            }
        }
        viewModelScope.launch {
            // Load default currency
            userPrefs.userPreferencesFlow.collect { prefs ->
                _uiState.value = _uiState.value.copy(selectedCurrency = prefs.defaultCurrency)
            }
        }
    }

    /** Append a digit or operator to the calculator expression */
    fun appendToExpression(input: String) {
        val current = _uiState.value.calculatorExpression
        // Prevent double operators
        val operators = setOf("+", "-", "×", "÷")
        if (input in operators && current.isNotEmpty() && current.last().toString() in operators.map { 
            when(it) { "×" -> "×"; "÷" -> "÷"; else -> it }
        }) {
            // Replace the last operator
            _uiState.value = _uiState.value.copy(calculatorExpression = current.dropLast(1) + input)
            return
        }
        // Prevent multiple dots in the same number
        if (input == ".") {
            val lastNumber = current.split(Regex("[+\\-×÷]")).lastOrNull() ?: ""
            if ("." in lastNumber) return
        }
        _uiState.value = _uiState.value.copy(calculatorExpression = current + input)
    }

    /** Delete the last character from the expression */
    fun backspace() {
        val current = _uiState.value.calculatorExpression
        if (current.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(calculatorExpression = current.dropLast(1))
        }
    }

    fun setNote(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun setDate(value: Long) {
        _uiState.value = _uiState.value.copy(selectedDate = value)
    }

    fun setCategory(category: CategoryEntity) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun setAccount(account: AccountEntity) {
        _uiState.value = _uiState.value.copy(selectedAccount = account)
    }

    fun setCurrency(currency: String) {
        _uiState.value = _uiState.value.copy(selectedCurrency = currency)
    }

    /** Evaluate the mathematical expression in the calculator */
    fun evaluateExpression() {
        val expr = _uiState.value.calculatorExpression
        if (expr.isEmpty()) return

        try {
            val result = evaluateMathExpression(expr)
            val formatted = if (result == result.toLong().toDouble()) {
                result.toLong().toString()
            } else {
                String.format("%.2f", result)
            }
            _uiState.value = _uiState.value.copy(
                calculatorExpression = formatted,
                amount = formatted
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = "Invalid expression")
        }
    }

    /** Save the transaction to the database */
    fun saveTransaction() {
        val state = _uiState.value

        // Evaluate expression first if needed
        val amountStr = state.calculatorExpression
        val amount = amountStr.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            _uiState.value = state.copy(error = "Please enter a valid amount")
            return
        }

        if (state.selectedCategory == null) {
            _uiState.value = state.copy(error = "Please select a category")
            return
        }

        viewModelScope.launch {
            val transaction = TransactionEntity(
                id = UUID.randomUUID().toString(),
                type = state.type,
                amount = amount,
                currency = state.selectedCurrency,
                categoryId = state.selectedCategory.id,
                accountId = state.selectedAccount?.id ?: "",
                note = state.note.takeIf { it.isNotBlank() },
                date = state.selectedDate,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            transactionRepo.insertTransaction(transaction)
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }

    /**
     * Simple math expression evaluator supporting +, -, ×, ÷
     * Processes multiplication/division first (operator precedence), then addition/subtraction.
     */
    private fun evaluateMathExpression(expression: String): Double {
        // Replace display operators with standard ones
        val normalized = expression.replace("×", "*").replace("÷", "/")

        // Tokenize: split into numbers and operators
        val tokens = mutableListOf<String>()
        var currentNumber = StringBuilder()

        for (i in normalized.indices) {
            val char = normalized[i]
            if (char in listOf('+', '-', '*', '/') && i > 0) {
                if (currentNumber.isNotEmpty()) {
                    tokens.add(currentNumber.toString())
                    currentNumber = StringBuilder()
                }
                tokens.add(char.toString())
            } else {
                currentNumber.append(char)
            }
        }
        if (currentNumber.isNotEmpty()) {
            tokens.add(currentNumber.toString())
        }

        if (tokens.isEmpty()) return 0.0

        // First pass: handle * and /
        val reduced = mutableListOf<String>()
        var i = 0
        while (i < tokens.size) {
            if (tokens[i] == "*" || tokens[i] == "/") {
                val left = reduced.removeLast().toDouble()
                val right = tokens[i + 1].toDouble()
                val result = if (tokens[i] == "*") left * right else left / right
                reduced.add(result.toString())
                i += 2
            } else {
                reduced.add(tokens[i])
                i++
            }
        }

        // Second pass: handle + and -
        var result = reduced[0].toDouble()
        var j = 1
        while (j < reduced.size) {
            val op = reduced[j]
            val right = reduced[j + 1].toDouble()
            result = if (op == "+") result + right else result - right
            j += 2
        }

        return result
    }
}
