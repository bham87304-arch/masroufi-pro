package com.masroufi.pro.ui.screen.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masroufi.pro.R

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.sign_in), style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.setEmail(it) },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.setPassword(it) },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = { viewModel.signIn() }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.sign_in))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { viewModel.toggleMode() }) {
            Text(stringResource(R.string.no_account_sign_up))
        }
    }
}
