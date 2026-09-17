package com.masroufi.pro.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.masroufi.pro.data.model.Currency
import com.masroufi.pro.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                ListItem(
                    headlineContent = { Text("Currency") },
                    supportingContent = { Text(uiState.currency) },
                    leadingContent = { Icon(Icons.Default.Paid, contentDescription = null) },
                    modifier = Modifier.clickable { viewModel.showCurrencyDialog() }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Language") },
                    supportingContent = {
                        val langText = when(uiState.language) {
                            "ar" -> "العربية"
                            "fr" -> "Français"
                            else -> "English"
                        }
                        Text(langText) 
                    },
                    leadingContent = { Icon(Icons.Default.Language, contentDescription = null) },
                    modifier = Modifier.clickable { viewModel.showLanguageDialog() }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Theme") },
                    supportingContent = { Text(uiState.themeMode.replaceFirstChar { it.uppercase() }) },
                    leadingContent = { Icon(Icons.Default.ColorLens, contentDescription = null) },
                    modifier = Modifier.clickable { viewModel.showThemeDialog() }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Daily Reminder") },
                    leadingContent = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    trailingContent = {
                        Switch(
                            checked = uiState.isReminderEnabled,
                            onCheckedChange = { viewModel.toggleReminder(it) }
                        )
                    }
                )
            }
            
            item { HorizontalDivider() }
            
            item {
                ListItem(
                    headlineContent = { Text("Manage Categories") },
                    leadingContent = { Icon(Icons.Default.Category, contentDescription = null) },
                    modifier = Modifier.clickable { navController.navigate(Screen.Categories.route) }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Manage Accounts") },
                    leadingContent = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                    modifier = Modifier.clickable { navController.navigate(Screen.Accounts.route) }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Sign In / Sync") },
                    leadingContent = { Icon(Icons.Default.Login, contentDescription = null) },
                    modifier = Modifier.clickable { navController.navigate(Screen.Auth.route) }
                )
            }
            
            item { HorizontalDivider() }
            
            item {
                ListItem(
                    headlineContent = { Text("Clear Data") },
                    leadingContent = { Icon(Icons.Default.DeleteForever, contentDescription = null) },
                    modifier = Modifier.clickable { viewModel.showClearDataDialog() }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Version") },
                    supportingContent = { Text("1.0.0") },
                    leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }
                )
            }
        }
        
        if (uiState.showCurrencyDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideCurrencyDialog() },
                title = { Text("Select Currency") },
                text = {
                    LazyColumn {
                        val currencies = Currency.getSupportedCurrencies()
                        items(currencies.size) { index ->
                            val currency = currencies[index]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setCurrency(currency.code) }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = currency.code == uiState.currency,
                                    onClick = { viewModel.setCurrency(currency.code) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${currency.symbol} ${currency.name} (${currency.code})")
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.hideCurrencyDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        if (uiState.showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideLanguageDialog() },
                title = { Text("Select Language") },
                text = {
                    Column {
                        val languages = listOf(
                            "en" to "English",
                            "ar" to "العربية",
                            "fr" to "Français"
                        )
                        languages.forEach { (code, name) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setLanguage(code) }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = code == uiState.language,
                                    onClick = { viewModel.setLanguage(code) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(name)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.hideLanguageDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        if (uiState.showThemeDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideThemeDialog() },
                title = { Text("Select Theme") },
                text = {
                    Column {
                        listOf("system", "light", "dark").forEach { mode ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setTheme(mode) }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = mode == uiState.themeMode,
                                    onClick = { viewModel.setTheme(mode) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(mode.replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.hideThemeDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        if (uiState.showClearDataDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideClearDataDialog() },
                title = { Text("Clear All Data") },
                text = { Text("Are you sure you want to delete all data? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.clearData() }
                    ) {
                        Text("Clear", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideClearDataDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
