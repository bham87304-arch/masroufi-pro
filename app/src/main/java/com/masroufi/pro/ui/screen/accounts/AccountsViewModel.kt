package com.masroufi.pro.ui.screen.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.local.entity.AccountEntity
import com.masroufi.pro.data.model.TransactionType
import com.masroufi.pro.data.repository.AccountRepository
import com.masroufi.pro.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

data class AccountWithBalance(val account: AccountEntity, val balance: Double)

data class AccountsUiState(
    val accounts: List<AccountWithBalance> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingAccount: AccountEntity? = null
)

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                accountRepository.getAllAccounts(),
                transactionRepository.getAllTransactions()
            ) { accounts, transactions ->
                val accountWithBalances = accounts.map { account ->
                    val accountTransactions = transactions.filter { it.accountId == account.id && !it.isDeleted }
                    val income = accountTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                    val expense = accountTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                    val balance = account.initialBalance + income - expense
                    AccountWithBalance(account, balance)
                }
                accountWithBalances
            }.collect { balances ->
                _uiState.update { it.copy(accounts = balances) }
            }
        }
    }

    fun addAccount(account: AccountEntity) {
        viewModelScope.launch {
            accountRepository.insertAccount(
                account.copy(id = UUID.randomUUID().toString())
            )
        }
    }

    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch {
            accountRepository.updateAccount(account)
        }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            accountRepository.deleteAccount(account.id)
        }
    }

    fun setShowAddDialog(show: Boolean) {
        _uiState.update { it.copy(showAddDialog = show) }
    }

    fun setEditingAccount(account: AccountEntity?) {
        _uiState.update { it.copy(editingAccount = account) }
    }
}
