package com.masroufi.pro.ui.screen.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masroufi.pro.R
import com.masroufi.pro.data.model.TransactionType
import com.masroufi.pro.ui.components.AmountText
import com.masroufi.pro.ui.components.CategoryIcon
import com.masroufi.pro.ui.components.DonutChart
import com.masroufi.pro.ui.components.DonutSlice

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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Period selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val periods = listOf("Month", "Year", "All")
                    periods.forEach { period ->
                        val labelResId = when (period) {
                            "Month" -> R.string.period_month
                            "Year" -> R.string.period_year
                            else -> R.string.period_all
                        }
                        FilterChip(
                            selected = uiState.selectedPeriod == period,
                            onClick = { viewModel.changePeriod(period) },
                            label = { Text(stringResource(labelResId)) }
                        )
                    }
                }
            }

            item {
                // Summary card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(stringResource(R.string.total_income), style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            AmountText(
                                amount = uiState.totalIncome,
                                currency = "DZD",
                                type = TransactionType.INCOME
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(stringResource(R.string.total_expenses), style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            AmountText(
                                amount = uiState.totalExpenses,
                                currency = "DZD",
                                type = TransactionType.EXPENSE
                            )
                        }
                    }
                }
            }
            
            item {
                // Donut Chart
                if (uiState.categoryBreakdown.isNotEmpty() && uiState.totalExpenses > 0) {
                    val slices = uiState.categoryBreakdown.map {
                        val displayName = when (java.util.Locale.getDefault().language) {
                            "ar" -> it.category.nameAr.ifEmpty { it.category.name }
                            "fr" -> it.category.nameFr.ifEmpty { it.category.name }
                            else -> it.category.name
                        }
                        DonutSlice(
                            label = displayName,
                            value = it.total.toFloat(),
                            color = Color(it.category.color)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        DonutChart(
                            slices = slices,
                            totalAmount = uiState.totalExpenses,
                            centerText = stringResource(R.string.total_expenses)
                        )
                    }
                }
            }

            if (uiState.categoryBreakdown.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.category_breakdown),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(uiState.categoryBreakdown) { categoryWithTotal ->
                    CategoryBreakdownItem(
                        item = categoryWithTotal,
                        totalExpenses = uiState.totalExpenses
                    )
                }
            }

            if (uiState.topCategories.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.top_spending),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(uiState.topCategories) { categoryWithTotal ->
                    TopCategoryItem(item = categoryWithTotal)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    stringResource(R.string.monthly_comparison),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.this_month))
                            AmountText(amount = uiState.currentMonthExpenses, currency = "DZD", type = TransactionType.EXPENSE)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.last_month))
                            AmountText(amount = uiState.previousMonthExpenses, currency = "DZD", type = TransactionType.EXPENSE)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        val isIncrease = uiState.comparisonData > 0
                        val color = if (isIncrease) Color.Red else Color.Green
                        val text = if (isIncrease) "+${String.format("%.1f", uiState.comparisonData)}%" else "${String.format("%.1f", uiState.comparisonData)}%"
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.change_label))
                            Text(text, color = color, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CategoryBreakdownItem(item: CategoryWithTotal, totalExpenses: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryIcon(
            iconName = item.category.icon,
            color = Color(item.category.color),
            size = 40.dp
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val displayName = when (java.util.Locale.getDefault().language) {
                    "ar" -> item.category.nameAr.ifEmpty { item.category.name }
                    "fr" -> item.category.nameFr.ifEmpty { item.category.name }
                    else -> item.category.name
                }
                Text(displayName, style = MaterialTheme.typography.bodyLarge)
                AmountText(amount = item.total, currency = "DZD", type = TransactionType.EXPENSE)
            }
            Spacer(modifier = Modifier.height(4.dp))
            val percentage = if (totalExpenses > 0) (item.total / totalExpenses).toFloat() else 0f
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { percentage },
                    modifier = Modifier.weight(1f).height(8.dp),
                    color = Color(item.category.color),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TopCategoryItem(item: CategoryWithTotal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CategoryIcon(
                iconName = item.category.icon,
                color = Color(item.category.color),
                size = 32.dp
            )
            Spacer(modifier = Modifier.width(16.dp))
            val displayName = when (java.util.Locale.getDefault().language) {
                "ar" -> item.category.nameAr.ifEmpty { item.category.name }
                "fr" -> item.category.nameFr.ifEmpty { item.category.name }
                else -> item.category.name
            }
            Text(displayName)
        }
        AmountText(amount = item.total, currency = "DZD", type = TransactionType.EXPENSE)
    }
}
