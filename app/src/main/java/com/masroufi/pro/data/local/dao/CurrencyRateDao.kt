package com.masroufi.pro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.masroufi.pro.data.local.entity.CurrencyRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyRateDao {
    @Query("SELECT * FROM currency_rates")
    fun getAllRates(): Flow<List<CurrencyRateEntity>>

    @Query("SELECT rate FROM currency_rates WHERE fromCurrency = :from AND toCurrency = :to")
    suspend fun getRate(from: String, to: String): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRate(rate: CurrencyRateEntity)

    @Query("DELETE FROM currency_rates WHERE fromCurrency = :from AND toCurrency = :to")
    suspend fun deleteRate(from: String, to: String)
}
