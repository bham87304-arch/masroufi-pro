package com.masroufi.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val hour: Int = 20,
    val minute: Int = 0,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
