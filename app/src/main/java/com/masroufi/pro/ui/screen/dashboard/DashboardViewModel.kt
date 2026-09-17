package com.masroufi.pro.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.local.entity.CategoryEntity
import com.masroufi.pro.data.local.entity.TransactionEntity
import com.masroufi.pro.data.model.TransactionType
import com.masroufi.pro.data.repository.AccountRepository
import com.masroufi.pro.data.repository.CategoryRepository
import com.masroufi.pro.data.repository.TransactionRepository
import com.masroufi.pro.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
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

    private val _selectedPeriod = MutableStateFlow("Month")

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = combine(
        _selectedPeriod,
        userPrefs.userPreferencesFlow,
        _selectedPeriod.flatMapLatest { period ->
            val (start, end) = getDateRange(period)
            combine(
                combine(
                    transactionRepo.getTotalByTypeAndDateRange(TransactionType.INCOME, start, end),
                    transactionRepo.getTotalByTypeAndDateRange(TransactionType.EXPENSE, start, end),
                    transactionRepo.getCategoryTotals(TransactionType.EXPENSE, start, end)
                ) { income, expense, catTotals -> Triple(income, expense, catTotals) },
                combine(
                    transactionRepo.getTransactionsByDateRange(start, end),
                    categoryRepo.getAllCategories()
                ) { transactions, categories -> Pair(transactions, categories) }
            ) { triple, pair ->
                val income = triple.first ?: 0.0
                val expense = triple.second ?: 0.0
                val catTotals = triple.third
                val transactions = pair.first
                val categories = pair.second
                
                val balance = income - expense
                
                val catMap = categories.associateBy { it.id }
                
                val categoryTotals = catTotals.mapNotNull { catTotal ->
                    val cat = catMap[catTotal.categoryId]
                    if (cat != null) {
                        val percentage = if (expense > 0) (catTotal.total / expense * 100).toFloat() else 0f
                        CategoryWithTotal(cat, catTotal.total, percentage)
                    } else null
                }.sortedByDescending { it.total }
                
                val recentTrans = transactions
                    .sortedByDescending { it.date }
                    .take(5)
                    .mapNotNull { t ->
                        val cat = catMap[t.categoryId]
                        if (cat != null) {
                            TransactionWithCategory(t, cat)
                        } else null
                    }
                
                DashboardData(income, expense, balance, categoryTotals, recentTrans)
            }
        }
    ) { period, prefs, data ->
        DashboardUiState(
            totalIncome = data.income,
            totalExpenses = data.expense,
            balance = data.balance,
            categoryTotals = data.categoryTotals,
            recentTransactions = data.recentTrans,
            selectedPeriod = period,
            defaultCurrency = prefs.defaultCurrency,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    private data class DashboardData(
        val income: Double,
        val expense: Double,
        val balance: Double,
        val categoryTotals: List<CategoryWithTotal>,
        val recentTrans: List<TransactionWithCategory>
    )

    fun changePeriod(period: String) {
        _selectedPeriod.value = period
    }

    private fun getDateRange(period: String): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val end = calendar.timeInMillis
        
        when (period) {
            "Day" -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            "Week" -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            "Month" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            "Year" -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            else -> return Pair(0L, Long.MAX_VALUE) // All
        }
        
        return Pair(calendar.timeInMillis, end)
    }
}
