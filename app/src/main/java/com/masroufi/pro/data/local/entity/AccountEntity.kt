package com.masroufi.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val currency: String = "DZD",
    val icon: String = "account_balance_wallet",
    val color: Long = 0xFF2E7D32,
    val initialBalance: Double = 0.0,
    val isDefault: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
