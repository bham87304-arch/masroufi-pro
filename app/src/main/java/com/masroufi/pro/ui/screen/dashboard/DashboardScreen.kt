package com.masroufi.pro.ui.screen.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.masroufi.pro.R
import com.masroufi.pro.ui.components.AmountText
import com.masroufi.pro.ui.components.DonutChart
import com.masroufi.pro.ui.components.DonutSlice
import com.masroufi.pro.ui.components.TransactionCard
import com.masroufi.pro.ui.navigation.Screen

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Period Selector
        val periods = listOf("Day", "Week", "Month", "Year", "All")
        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(periods) { period ->
                val displayPeriod = when(period) {
                    "Day" -> stringResource(R.string.period_day)
                    "Week" -> stringResource(R.string.period_week)
                    "Month" -> stringResource(R.string.period_month)
                    "Year" -> stringResource(R.string.period_year)
                    else -> stringResource(R.string.period_all)
                }
                FilterChip(
                    selected = state.selectedPeriod == period,
                    onClick = { viewModel.changePeriod(period) },
                    label = { Text(displayPeriod) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Summary Cards (simplified for space)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.balance))
                AmountText(amount = state.balance, currency = state.defaultCurrency, type = null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Donut Chart
        if (state.categoryTotals.isNotEmpty()) {
            val slices = state.categoryTotals.map {
                val cat = it.category
                val displayName = when (java.util.Locale.getDefault().language) {
                    "ar" -> cat.nameAr.ifEmpty { cat.name }
                    "fr" -> cat.nameFr.ifEmpty { cat.name }
                    else -> cat.name
                }
                DonutSlice(displayName, it.percentage, Color(it.category.color)) 
            }
            DonutChart(
                slices = slices,
                totalAmount = state.totalExpenses,
                centerText = stringResource(R.string.filter_expense),
                modifier = Modifier.size(200.dp).padding(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(stringResource(R.string.recent_transactions), style = MaterialTheme.typography.titleMedium)
        
        // Recent Transactions
        state.recentTransactions.take(5).forEach { tc ->
            TransactionCard(
                transaction = tc.transaction,
                category = tc.category,
                onClick = {
                    navController.navigate(Screen.AddTransaction.createRoute(tc.transaction.type))
                },
                onSwipeToDelete = { viewModel.deleteTransaction(tc.transaction.id) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
