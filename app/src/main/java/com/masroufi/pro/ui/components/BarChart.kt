package com.masroufi.pro.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.masroufi.pro.ui.theme.ExpenseColor
import com.masroufi.pro.ui.theme.IncomeColor

data class BarData(val label: String, val incomeValue: Float, val expenseValue: Float)

@Composable
fun BarChart(
    data: List<BarData>,
    maxValue: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val barWidth = size.width / (data.size * 2.5f)
        val spacing = barWidth * 0.5f
        
        data.forEachIndexed { index, item ->
            val startX = index * (barWidth * 2 + spacing)
            val incomeHeight = (item.incomeValue / maxValue) * size.height
            val expenseHeight = (item.expenseValue / maxValue) * size.height
            
            // Income Bar
            drawRoundRect(
                color = IncomeColor,
                topLeft = Offset(startX, size.height - incomeHeight),
                size = Size(barWidth, incomeHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )
            
            // Expense Bar
            drawRoundRect(
                color = ExpenseColor,
                topLeft = Offset(startX + barWidth, size.height - expenseHeight),
                size = Size(barWidth, expenseHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}
