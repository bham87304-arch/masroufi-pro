package com.masroufi.pro.data.repository

import com.masroufi.pro.data.local.dao.CategoryTotal
import com.masroufi.pro.data.local.dao.TransactionDao
import com.masroufi.pro.data.local.entity.TransactionEntity
import com.masroufi.pro.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getAllTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate)

    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByType(type)

    fun getTransactionsByCategory(categoryId: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByCategory(categoryId)

    fun getTransactionsByAccount(accountId: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByAccount(accountId)

    suspend fun getTransactionById(id: String): TransactionEntity? =
        transactionDao.getTransactionById(id)

    suspend fun insertTransaction(transaction: TransactionEntity) {
        val now = System.currentTimeMillis()
        val newTransaction = transaction.copy(
            id = transaction.id.ifEmpty { UUID.randomUUID().toString() },
            createdAt = if (transaction.createdAt == 0L) now else transaction.createdAt,
            updatedAt = now
        )
        transactionDao.insertTransaction(newTransaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteTransaction(id: String) {
        transactionDao.softDeleteTransaction(id, System.currentTimeMillis())
    }

    fun getTotalByType(type: TransactionType): Flow<Double?> =
        transactionDao.getTotalByType(type)

    fun getTotalByTypeAndDateRange(type: TransactionType, startDate: Long, endDate: Long): Flow<Double?> =
        transactionDao.getTotalByTypeAndDateRange(type, startDate, endDate)

    fun getCategoryTotals(type: TransactionType, startDate: Long, endDate: Long): Flow<List<CategoryTotal>> =
        transactionDao.getCategoryTotals(type, startDate, endDate)

    fun searchTransactions(query: String): Flow<List<TransactionEntity>> =
        transactionDao.searchTransactions(query)
}
