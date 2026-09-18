package com.masroufi.pro.ui.screen.accounts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masroufi.pro.R
import com.masroufi.pro.data.local.entity.AccountEntity
import com.masroufi.pro.ui.components.CategoryIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var accountToDelete by remember { mutableStateOf<AccountEntity?>(null) }

    val totalBalance = uiState.accounts.sumOf { it.balance }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.accounts)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.setEditingAccount(null)
                        viewModel.setShowAddDialog(true) 
                    }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_account))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.total_balance), style = MaterialTheme.typography.titleMedium)
                        Text(
                            String.format("%.2f DZD", totalBalance),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
            items(uiState.accounts) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            if (!item.account.isDefault) {
                                viewModel.setEditingAccount(item.account)
                                viewModel.setShowAddDialog(true)
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryIcon(
                            iconName = item.account.icon,
                            color = Color(item.account.color),
                            size = 48.dp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.account.name, style = MaterialTheme.typography.titleMedium)
                            Text(text = item.account.currency, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            text = String.format("%.2f", item.balance),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    if (uiState.showAddDialog) {
        AccountDialog(
            account = uiState.editingAccount,
            onDismiss = { viewModel.setShowAddDialog(false) },
            onSave = {
                if (uiState.editingAccount != null) {
                    viewModel.updateAccount(it)
                } else {
                    viewModel.addAccount(it)
                }
                viewModel.setShowAddDialog(false)
            },
            onDelete = {
                accountToDelete = it
                viewModel.setShowAddDialog(false)
            }
        )
    }

    accountToDelete?.let { account ->
        AlertDialog(
            onDismissRequest = { accountToDelete = null },
            title = { Text(stringResource(R.string.delete_account)) },
            text = { Text(stringResource(R.string.confirm_delete)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAccount(account)
                    accountToDelete = null
                }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { accountToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDialog(
    account: AccountEntity?,
    onDismiss: () -> Unit,
    onSave: (AccountEntity) -> Unit,
    onDelete: (AccountEntity) -> Unit
) {
    var name by remember { mutableStateOf(account?.name ?: "") }
    var initialBalance by remember { mutableStateOf(account?.initialBalance?.toString() ?: "0.0") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (account == null) stringResource(R.string.add_account) else stringResource(R.string.edit_account)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = initialBalance,
                    onValueChange = { initialBalance = it },
                    label = { Text(stringResource(R.string.initial_balance)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val balance = initialBalance.toDoubleOrNull() ?: 0.0
                        val newAccount = account?.copy(
                            name = name,
                            initialBalance = balance
                        ) ?: AccountEntity(
                            name = name,
                            initialBalance = balance
                        )
                        onSave(newAccount)
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            Row {
                if (account != null && !account.isDefault) {
                    TextButton(onClick = { onDelete(account) }) {
                        Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    )
}
