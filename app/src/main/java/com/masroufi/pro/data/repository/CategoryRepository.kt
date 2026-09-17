package com.masroufi.pro.data.repository

import com.masroufi.pro.data.local.dao.CategoryDao
import com.masroufi.pro.data.local.entity.CategoryEntity
import com.masroufi.pro.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>> = categoryDao.getCategoriesByType(type)

    suspend fun getCategoryById(id: String): CategoryEntity? = categoryDao.getCategoryById(id)

    suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(id: String) {
        categoryDao.deleteCategory(id)
    }
}
