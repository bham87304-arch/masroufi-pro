package com.masroufi.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.masroufi.pro.data.model.TransactionType
import java.util.UUID

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val nameAr: String = "",
    val nameFr: String = "",
    val icon: String,
    val color: Long,
    val type: TransactionType,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0
)
