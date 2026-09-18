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
        // Shopping & Food
        "shopping_cart" -> Icons.Default.ShoppingCart
        "fastfood" -> Icons.Default.Fastfood
        "restaurant" -> Icons.Default.Restaurant
        "local_dining" -> Icons.Default.LocalDining
        "coffee" -> Icons.Default.LocalCafe

        // Transport & Travel
        "directions_car" -> Icons.Default.DirectionsCar
        "flight" -> Icons.Default.Flight
        "local_gas_station" -> Icons.Default.LocalGasStation
        "place" -> Icons.Default.Place

        // Home & Living
        "home" -> Icons.Default.Home
        "build" -> Icons.Default.Build

        // Health & Wellness
        "health_and_safety" -> Icons.Default.HealthAndSafety
        "medical_services" -> Icons.Default.MedicalServices
        "spa" -> Icons.Default.Spa
        "fitness" -> Icons.Default.FitnessCenter
        "favorite" -> Icons.Default.Favorite

        // People & Education
        "person" -> Icons.Default.Person
        "school" -> Icons.Default.School
        "checkroom" -> Icons.Default.Checkroom

        // Money & Finance
        "attach_money" -> Icons.Default.AttachMoney
        "payments" -> Icons.Default.Payments
        "account_balance" -> Icons.Default.AccountBalance
        "account_balance_wallet" -> Icons.Default.AccountBalanceWallet
        "savings" -> Icons.Default.Savings
        "trending_up" -> Icons.Default.TrendingUp

        // Work & Business
        "work" -> Icons.Default.Work
        "card_giftcard" -> Icons.Default.CardGiftcard

        // Communication & Tech
        "phone" -> Icons.Default.Phone
        "email" -> Icons.Default.Email
        "wifi" -> Icons.Default.Wifi

        // Entertainment & Hobbies
        "sports_esports" -> Icons.Default.SportsEsports
        "movie" -> Icons.Default.Movie
        "music_note" -> Icons.Default.MusicNote
        "camera" -> Icons.Default.CameraAlt
        "book" -> Icons.Default.Book
        "brush" -> Icons.Default.Brush
        "pets" -> Icons.Default.Pets

        // General
        "star" -> Icons.Default.Star
        "search" -> Icons.Default.Search
        "settings" -> Icons.Default.Settings
        "more_horiz" -> Icons.Default.MoreHoriz
        "category" -> Icons.Default.Category
        "notifications" -> Icons.Default.Notifications
        "lock" -> Icons.Default.Lock
        "cloud" -> Icons.Default.Cloud
        "calendar" -> Icons.Default.DateRange
        "lightbulb" -> Icons.Default.Lightbulb

        else -> Icons.Default.Category
    }
}
