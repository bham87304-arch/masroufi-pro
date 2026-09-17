package com.masroufi.pro.ui.screen.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.local.entity.CategoryEntity
import com.masroufi.pro.data.model.TransactionType
import com.masroufi.pro.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CategoriesUiState(
    val expenseCategories: List<CategoryEntity> = emptyList(),
    val incomeCategories: List<CategoryEntity> = emptyList(),
    val selectedTab: Int = 0,
    val showAddDialog: Boolean = false,
    val editingCategory: CategoryEntity? = null
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { state ->
                    state.copy(
                        expenseCategories = categories.filter { it.type == TransactionType.EXPENSE },
                        incomeCategories = categories.filter { it.type == TransactionType.INCOME }
                    )
                }
            }
        }
    }

    fun addCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.insertCategory(
                category.copy(id = UUID.randomUUID().toString())
            )
        }
    }

    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category.id)
        }
    }

    fun setTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
    
    fun setShowAddDialog(show: Boolean) {
        _uiState.update { it.copy(showAddDialog = show) }
    }
    
    fun setEditingCategory(category: CategoryEntity?) {
        _uiState.update { it.copy(editingCategory = category) }
    }
}
