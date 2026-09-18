package com.masroufi.pro.ui.screen.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.local.entity.CategoryEntity
import com.masroufi.pro.data.model.TransactionType
import com.masroufi.pro.data.repository.CategoryRepository
import com.masroufi.pro.data.repository.TransactionRepository
import com.masroufi.pro.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.Calendar
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
    val currentMonthExpenses: Double = 0.0,
    val previousMonthExpenses: Double = 0.0,
    val comparisonData: Double = 0.0
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow("Month")

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<StatsUiState> = _selectedPeriod.flatMapLatest { period ->
        val (startDate, endDate) = getDateRange(period)
        
        val incomeFlow = transactionRepository.getTotalByTypeAndDateRange(TransactionType.INCOME, startDate, endDate).map { it ?: 0.0 }
        val expensesFlow = transactionRepository.getTotalByTypeAndDateRange(TransactionType.EXPENSE, startDate, endDate).map { it ?: 0.0 }
        
        val categoryBreakdownFlow = transactionRepository.getCategoryTotals(TransactionType.EXPENSE, startDate, endDate).map { totals ->
            totals.mapNotNull { categoryTotal ->
                categoryRepository.getCategoryById(categoryTotal.categoryId)?.let { category ->
                    CategoryWithTotal(category, categoryTotal.total)
                }
            }
        }
        
        val currentMonthRange = getDateRange("Month")
        val previousMonthRange = getPreviousMonthDateRange()
        
        val currentMonthExpFlow = transactionRepository.getTotalByTypeAndDateRange(TransactionType.EXPENSE, currentMonthRange.first, currentMonthRange.second).map { it ?: 0.0 }
        val prevMonthExpFlow = transactionRepository.getTotalByTypeAndDateRange(TransactionType.EXPENSE, previousMonthRange.first, previousMonthRange.second).map { it ?: 0.0 }

        combine(
            incomeFlow,
            expensesFlow,
            categoryBreakdownFlow,
            currentMonthExpFlow,
            prevMonthExpFlow
        ) { income, expenses, breakdown, currentExp, prevExp ->
            val sortedBreakdown = breakdown.sortedByDescending { it.total }
            val topCategories = sortedBreakdown.take(5)
            
            StatsUiState(
                totalIncome = income,
                totalExpenses = expenses,
                categoryBreakdown = sortedBreakdown,
                topCategories = topCategories,
                selectedPeriod = period,
                currentMonthExpenses = currentExp,
                previousMonthExpenses = prevExp,
                comparisonData = if (prevExp > 0) ((currentExp - prevExp) / prevExp) * 100 else 0.0
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsUiState())

    fun changePeriod(period: String) {
        _selectedPeriod.value = period
    }

    private fun getDateRange(period: String): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        val startDate = when (period) {
            "Month" -> { 
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis 
            }
            "Year" -> { 
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis 
            }
            else -> 0L
        }
        return Pair(startDate, endDate)
    }
    
    private fun getPreviousMonthDateRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        calendar.add(Calendar.MONTH, -1)
        val startDate = calendar.timeInMillis
        
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val endDate = calendar.timeInMillis
        
        return Pair(startDate, endDate)
    }
}
