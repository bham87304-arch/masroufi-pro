package com.masroufi.pro.data.repository

import com.masroufi.pro.data.local.dao.CurrencyRateDao
import com.masroufi.pro.data.model.Currency
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val currencyRateDao: CurrencyRateDao
) {
    fun getSupportedCurrencies(): List<Currency> = Currency.getSupportedCurrencies()

    suspend fun getRate(from: String, to: String): Double? {
        if (from == to) return 1.0
        return currencyRateDao.getRate(from, to)
    }

    suspend fun convertAmount(amount: Double, from: String, to: String, fallbackRate: Double = 1.0): Double {
        if (from == to) return amount
        val rate = getRate(from, to) ?: fallbackRate
        return amount * rate
    }
}
