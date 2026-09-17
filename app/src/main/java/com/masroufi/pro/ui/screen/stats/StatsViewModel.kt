package com.masroufi.pro.ui.screen.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.local.entity.CategoryEntity
import com.masroufi.pro.data.repository.CategoryRepository
import com.masroufi.pro.data.repository.TransactionRepository
import com.masroufi.pro.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BarData(val label: String, val income: Double, val expense: Double)
data class CategoryWithTotal(val category: CategoryEntity, val total: Double)

data class StatsUiState(
    val monthlyData: List<BarData> = emptyList(),
    val categoryBreakdown: List<CategoryWithTotal> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val topCategories: List<CategoryWithTotal> = emptyList(),
    val selectedPeriod: String = "Month",
    val comparisonData: Double = 0.0
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun changePeriod(period: String) {
        _uiState.update { it.copy(selectedPeriod = period) }
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            // Simplified load implementation
        }
    }
}
