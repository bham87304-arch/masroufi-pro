package com.masroufi.pro.ui.screen.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.local.entity.AccountEntity
import com.masroufi.pro.data.repository.AccountRepository
import com.masroufi.pro.data.repository.TransactionRepository
import com.masroufi.pro.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountWithBalance(val account: AccountEntity, val balance: Double)

data class AccountsUiState(
    val accounts: List<AccountWithBalance> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingAccount: AccountEntity? = null
)

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    fun addAccount(account: AccountEntity) {
        viewModelScope.launch {
            // implementation
        }
    }

    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch {
            // implementation
        }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            // implementation
        }
    }
}
