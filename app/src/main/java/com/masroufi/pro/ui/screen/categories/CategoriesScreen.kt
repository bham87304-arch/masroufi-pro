package com.masroufi.pro.ui.screen.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
fun CategoriesScreen(
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.categories)) },
                actions = {
                    IconButton(onClick = { /* show dialog */ }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_category))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = uiState.selectedTab) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.setTab(0) },
                    text = { Text(stringResource(R.string.expense_categories)) }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.setTab(1) },
                    text = { Text(stringResource(R.string.income_categories)) }
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(8.dp)
            ) {
                // Category Items
            }
        }
    }
}
