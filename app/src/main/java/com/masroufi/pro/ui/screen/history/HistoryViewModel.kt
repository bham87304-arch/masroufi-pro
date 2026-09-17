package com.masroufi.pro.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.model.TransactionType
import com.masroufi.pro.data.repository.CategoryRepository
import com.masroufi.pro.data.repository.TransactionRepository
import com.masroufi.pro.data.preferences.UserPreferencesManager
import com.masroufi.pro.ui.screen.dashboard.TransactionWithCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import javax.inject.Inject

data class HistoryUiState(
    val groupedTransactions: Map<String, List<TransactionWithCategory>> = emptyMap(),
    val searchQuery: String = "",
    val filterType: String = "ALL", // ALL, INCOME, EXPENSE
    val isLoading: Boolean = false,
    val totalForPeriod: Double = 0.0
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val categoryRepo: CategoryRepository,
    private val userPrefs: UserPreferencesManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filterType = MutableStateFlow("ALL")

    val uiState: StateFlow<HistoryUiState> = combine(
        combine(
            transactionRepo.getAllTransactions(),
            categoryRepo.getAllCategories()
        ) { t, c -> Pair(t, c) },
        _searchQuery,
        _filterType
    ) { pair, query, filter ->
        val transactions = pair.first
        val categories = pair.second
        
        val catMap = categories.associateBy { it.id }
        
        val filteredList = transactions.filter { t ->
            val matchesFilter = when (filter) {
                "INCOME" -> t.type == TransactionType.INCOME
                "EXPENSE" -> t.type == TransactionType.EXPENSE
                else -> true
            }
            val matchesSearch = if (query.isNotBlank()) {
                val noteMatch = t.note?.contains(query, ignoreCase = true) == true
                val catMatch = catMap[t.categoryId]?.name?.contains(query, ignoreCase = true) == true
                noteMatch || catMatch
            } else {
                true
            }
            matchesFilter && matchesSearch
        }

        val mapped = filteredList.mapNotNull { t ->
            val cat = catMap[t.categoryId]
            if (cat != null) {
                TransactionWithCategory(t, cat)
            } else null
        }.sortedByDescending { it.transaction.date }

        val grouped = mapped.groupBy { formatDate(it.transaction.date) }
        
        val total = mapped.sumOf {
            if (it.transaction.type == TransactionType.INCOME) it.transaction.amount
            else -it.transaction.amount
        }

        HistoryUiState(
            groupedTransactions = grouped,
            searchQuery = query,
            filterType = filter,
            isLoading = false,
            totalForPeriod = total
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(isLoading = true)
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(type: String) {
        _filterType.value = type
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            transactionRepo.deleteTransaction(id)
        }
    }

    private fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        
        calendar.timeInMillis = timestamp
        val transDay = calendar.get(Calendar.DAY_OF_YEAR)
        val transYear = calendar.get(Calendar.YEAR)
        
        return when {
            year == transYear && today == transDay -> "Today"
            year == transYear && today - transDay == 1 -> "Yesterday"
            else -> {
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
        }
    }
}
