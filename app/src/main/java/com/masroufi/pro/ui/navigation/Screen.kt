package com.masroufi.pro.ui.navigation

import com.masroufi.pro.data.model.TransactionType

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object History : Screen("history")
    object Stats : Screen("stats")
    object Settings : Screen("settings")
    object AddTransaction : Screen("add_transaction/{type}") {
        fun createRoute(type: TransactionType) = "add_transaction/${type.name}"
    }
    object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(id: String) = "edit_transaction/$id"
    }
    object Categories : Screen("categories")
    object Accounts : Screen("accounts")
    object Auth : Screen("auth")
}
