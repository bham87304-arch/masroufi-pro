package com.masroufi.pro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CategoryIcon(
    iconName: String,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color.copy(alpha = 0.2f), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = mapIconName(iconName),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}

fun mapIconName(name: String): ImageVector {
    return when (name) {
        "shopping_cart" -> Icons.Default.ShoppingCart
        "home" -> Icons.Default.Home
        "directions_car" -> Icons.Default.DirectionsCar
        "fastfood" -> Icons.Default.Fastfood
        "health_and_safety" -> Icons.Default.Favorite
        "attach_money" -> Icons.Default.AttachMoney
        "account_balance_wallet" -> Icons.Default.AccountBalanceWallet
        "school" -> Icons.Default.CheckCircle // fallback for now
        "flight" -> Icons.Default.Place // fallback
        else -> Icons.Default.Category
    }
}
