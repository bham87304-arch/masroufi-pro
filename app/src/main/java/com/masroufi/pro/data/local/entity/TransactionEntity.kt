package com.masroufi.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.masroufi.pro.data.model.TransactionType
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: TransactionType,
    val amount: Double,
    val currency: String,
    val categoryId: String,
    val accountId: String,
    val note: String? = null,
    val date: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
