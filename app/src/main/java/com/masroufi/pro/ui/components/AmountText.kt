package com.masroufi.pro.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.masroufi.pro.data.model.TransactionType
import com.masroufi.pro.ui.theme.ExpenseColor
import com.masroufi.pro.ui.theme.IncomeColor

@Composable
fun AmountText(
    amount: Double,
    currency: String,
    type: TransactionType?,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    val color = when (type) {
        TransactionType.INCOME -> IncomeColor
        TransactionType.EXPENSE -> ExpenseColor
        null -> Color.Unspecified
    }
    
    val prefix = if (type == TransactionType.INCOME) "+" else if (type == TransactionType.EXPENSE) "-" else ""
    val formattedAmount = String.format("%.2f", amount)
    
    // In a real app, you'd map the currency code to its symbol using java.util.Currency
    val symbol = try {
        java.util.Currency.getInstance(currency).symbol
    } catch (e: Exception) {
        currency
    }

    Text(
        text = "$prefix$symbol$formattedAmount",
        color = color,
        style = style,
        modifier = modifier
    )
}
