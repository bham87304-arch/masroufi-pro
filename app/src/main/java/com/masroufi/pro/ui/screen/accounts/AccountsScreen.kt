package com.masroufi.pro.ui.screen.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masroufi.pro.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.accounts)) },
                actions = {
                    IconButton(onClick = { /* show dialog */ }) {
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
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.total_balance), style = MaterialTheme.typography.titleMedium)
                        Text("$0.00", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
            // Account Items
        }
    }
}
