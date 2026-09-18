package com.masroufi.pro.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.masroufi.pro.data.local.converter.Converters
import com.masroufi.pro.data.local.dao.AccountDao
import com.masroufi.pro.data.local.dao.CategoryDao
import com.masroufi.pro.data.local.dao.CurrencyRateDao
import com.masroufi.pro.data.local.dao.ReminderDao
import com.masroufi.pro.data.local.dao.TransactionDao
import com.masroufi.pro.data.local.entity.AccountEntity
import com.masroufi.pro.data.local.entity.CategoryEntity
import com.masroufi.pro.data.local.entity.CurrencyRateEntity
import com.masroufi.pro.data.local.entity.ReminderEntity
import com.masroufi.pro.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class,
        CurrencyRateEntity::class,
        ReminderEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MasroufiDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun currencyRateDao(): CurrencyRateDao
    abstract fun reminderDao(): ReminderDao
}
