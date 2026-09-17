package com.masroufi.pro.ui.screen.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masroufi.pro.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.statistics)) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.period_month))
                    Text(stringResource(R.string.period_year))
                    Text(stringResource(R.string.period_all))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.total_income) + ": ${uiState.totalIncome}")
                        Text(stringResource(R.string.total_expenses) + ": ${uiState.totalExpenses}")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.category_breakdown), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.top_spending), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.monthly_comparison), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
