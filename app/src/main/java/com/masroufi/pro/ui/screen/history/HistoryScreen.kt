package com.masroufi.pro.ui.screen.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.masroufi.pro.ui.components.TransactionCard

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = state.filterType == "ALL", onClick = { viewModel.setFilterType("ALL") }, label = { Text("All") })
            FilterChip(selected = state.filterType == "INCOME", onClick = { viewModel.setFilterType("INCOME") }, label = { Text("Income") })
            FilterChip(selected = state.filterType == "EXPENSE", onClick = { viewModel.setFilterType("EXPENSE") }, label = { Text("Expense") })
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            state.groupedTransactions.forEach { (date, transactions) ->
                item {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(transactions) { tc ->
                    TransactionCard(
                        transaction = tc.transaction,
                        category = tc.category,
                        onClick = {},
                        onSwipeToDelete = { viewModel.deleteTransaction(tc.transaction.id) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
