package com.masroufi.pro.ui.screen.categories

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masroufi.pro.R
import com.masroufi.pro.data.local.entity.CategoryEntity
import com.masroufi.pro.data.model.TransactionType
import com.masroufi.pro.ui.components.CategoryIcon

val PredefinedColors = listOf(
    0xFFF44336, 0xFFE91E63, 0xFF9C27B0, 0xFF673AB7, 
    0xFF3F51B5, 0xFF2196F3, 0xFF03A9F4, 0xFF00BCD4,
    0xFF009688, 0xFF4CAF50, 0xFF8BC34A, 0xFFCDDC39,
    0xFFFFEB3B, 0xFFFFC107, 0xFFFF9800, 0xFFFF5722
)

val PredefinedIcons = listOf(
    "shopping_cart", "home", "directions_car", "fastfood", 
    "health_and_safety", "attach_money", "account_balance_wallet", "school", "flight", "category"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var categoryToDelete by remember { mutableStateOf<CategoryEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.categories)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.setEditingCategory(null)
                        viewModel.setShowAddDialog(true) 
                    }) {
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
            
            val items = if (uiState.selectedTab == 0) uiState.expenseCategories else uiState.incomeCategories
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(8.dp)
            ) {
                items(items) { category ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .aspectRatio(1f)
                            .combinedClickable(
                                onClick = {
                                    if (!category.isDefault) {
                                        viewModel.setEditingCategory(category)
                                        viewModel.setShowAddDialog(true)
                                    }
                                },
                                onLongClick = {
                                    if (!category.isDefault) {
                                        categoryToDelete = category
                                    }
                                }
                            ),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CategoryIcon(
                                iconName = category.icon,
                                color = Color(category.color),
                                size = 48.dp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.showAddDialog) {
        CategoryDialog(
            category = uiState.editingCategory,
            defaultType = if (uiState.selectedTab == 0) TransactionType.EXPENSE else TransactionType.INCOME,
            onDismiss = { viewModel.setShowAddDialog(false) },
            onSave = { 
                if (uiState.editingCategory != null) {
                    viewModel.updateCategory(it)
                } else {
                    viewModel.addCategory(it)
                }
                viewModel.setShowAddDialog(false)
            }
        )
    }

    categoryToDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Delete Category") },
            text = { Text("Are you sure you want to delete ${category.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCategory(category)
                    categoryToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDialog(
    category: CategoryEntity?,
    defaultType: TransactionType,
    onDismiss: () -> Unit,
    onSave: (CategoryEntity) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var selectedIcon by remember { mutableStateOf(category?.icon ?: PredefinedIcons.first()) }
    var selectedColor by remember { mutableStateOf(category?.color ?: PredefinedColors.first()) }
    var type by remember { mutableStateOf(category?.type ?: defaultType) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "Add Category" else "Edit Category") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Type", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterChip(
                        selected = type == TransactionType.EXPENSE,
                        onClick = { type = TransactionType.EXPENSE },
                        label = { Text("Expense") }
                    )
                    FilterChip(
                        selected = type == TransactionType.INCOME,
                        onClick = { type = TransactionType.INCOME },
                        label = { Text("Income") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Icon", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(PredefinedIcons) { icon ->
                        val isSelected = selectedIcon == icon
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent, CircleShape)
                                .clickable { selectedIcon = icon },
                            contentAlignment = Alignment.Center
                        ) {
                            CategoryIcon(iconName = icon, color = MaterialTheme.colorScheme.onSurface, size = 32.dp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Color", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(PredefinedColors) { color ->
                        val isSelected = selectedColor == color
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(color), CircleShape)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val newCategory = category?.copy(
                            name = name,
                            icon = selectedIcon,
                            color = selectedColor,
                            type = type
                        ) ?: CategoryEntity(
                            name = name,
                            icon = selectedIcon,
                            color = selectedColor,
                            type = type
                        )
                        onSave(newCategory)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
