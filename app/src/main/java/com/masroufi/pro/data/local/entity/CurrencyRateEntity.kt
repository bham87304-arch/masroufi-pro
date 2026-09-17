package com.masroufi.pro.data.local.entity

import androidx.room.Entity

@Entity(tableName = "currency_rates", primaryKeys = ["fromCurrency", "toCurrency"])
data class CurrencyRateEntity(
    val fromCurrency: String,
    val toCurrency: String,
    val rate: Double,
    val updatedAt: Long = System.currentTimeMillis()
)
