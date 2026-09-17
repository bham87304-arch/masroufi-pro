package com.masroufi.pro.ui.screen.settings

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
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.settings)) })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            item {
                Text(stringResource(R.string.general), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                ListItem(headlineContent = { Text(stringResource(R.string.default_currency)) })
                ListItem(headlineContent = { Text(stringResource(R.string.language)) })
                ListItem(headlineContent = { Text(stringResource(R.string.theme)) })
                
                Divider()
                
                Text(stringResource(R.string.notifications), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                ListItem(headlineContent = { Text(stringResource(R.string.daily_reminder)) })
                
                Divider()

                Text(stringResource(R.string.data), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                ListItem(headlineContent = { Text(stringResource(R.string.export_csv)) })
                ListItem(headlineContent = { Text(stringResource(R.string.manage_categories)) })
                ListItem(headlineContent = { Text(stringResource(R.string.manage_accounts)) })
                ListItem(headlineContent = { Text(stringResource(R.string.clear_data)) })
                
                Divider()
                
                Text(stringResource(R.string.account_section), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                ListItem(headlineContent = { Text(stringResource(R.string.sign_in)) })
                
                Divider()
                
                Text(stringResource(R.string.about), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                ListItem(headlineContent = { Text(stringResource(R.string.version)) })
            }
        }
    }
}
