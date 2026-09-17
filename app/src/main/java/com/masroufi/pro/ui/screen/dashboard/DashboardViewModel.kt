package com.masroufi.pro.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.local.entity.CategoryEntity
import com.masroufi.pro.data.local.entity.TransactionEntity
import com.masroufi.pro.data.repository.AccountRepository
import com.masroufi.pro.data.repository.CategoryRepository
import com.masroufi.pro.data.repository.TransactionRepository
import com.masroufi.pro.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryWithTotal(val category: CategoryEntity, val total: Double, val percentage: Float)
data class TransactionWithCategory(val transaction: TransactionEntity, val category: CategoryEntity)

data class DashboardUiState(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0,
    val categoryTotals: List<CategoryWithTotal> = emptyList(),
    val recentTransactions: List<TransactionWithCategory> = emptyList(),
    val selectedPeriod: String = "Month",
    val defaultCurrency: String = "DZD",
    val isLoading: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val categoryRepo: CategoryRepository,
    private val accountRepo: AccountRepository,
    private val userPrefs: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun changePeriod(period: String) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period)
        loadData()
    }

    private fun loadData() {
        // Stub implementation: Combine flows from repositories
        // This is a simplified representation to avoid build errors if actual repositories are missing implementations
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Dummy data to prevent empty states in initial UI build
            _uiState.value = _uiState.value.copy(
                totalIncome = 5000.0,
                totalExpenses = 2000.0,
                balance = 3000.0,
                isLoading = false
            )
        }
    }
}
