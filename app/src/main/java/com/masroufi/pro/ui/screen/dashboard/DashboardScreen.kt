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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.masroufi.pro.ui.components.AmountText
import com.masroufi.pro.ui.components.DonutChart
import com.masroufi.pro.ui.components.DonutSlice
import com.masroufi.pro.ui.components.TransactionCard

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
                FilterChip(
                    selected = state.selectedPeriod == period,
                    onClick = { viewModel.changePeriod(period) },
                    label = { Text(period) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Summary Cards (simplified for space)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Balance")
                AmountText(amount = state.balance, currency = state.defaultCurrency, type = null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Donut Chart
        if (state.categoryTotals.isNotEmpty()) {
            val slices = state.categoryTotals.map { 
                DonutSlice(it.category.name, it.percentage, Color(it.category.color)) 
            }
            DonutChart(
                slices = slices,
                totalAmount = state.totalExpenses,
                centerText = "Expenses",
                modifier = Modifier.size(200.dp).padding(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Recent Transactions", style = MaterialTheme.typography.titleMedium)
        
        // Recent Transactions
        state.recentTransactions.take(5).forEach { tc ->
            TransactionCard(
                transaction = tc.transaction,
                category = tc.category,
                onClick = {},
                onSwipeToDelete = {},
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
