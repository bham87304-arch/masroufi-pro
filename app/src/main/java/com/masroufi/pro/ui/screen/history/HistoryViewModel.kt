package com.masroufi.pro.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.repository.CategoryRepository
import com.masroufi.pro.data.repository.TransactionRepository
import com.masroufi.pro.data.preferences.UserPreferencesManager
import com.masroufi.pro.ui.screen.dashboard.TransactionWithCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        // trigger reload/filter
    }

    fun setFilterType(type: String) {
        _uiState.value = _uiState.value.copy(filterType = type)
        // trigger reload/filter
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            // delete transaction
        }
    }
}
