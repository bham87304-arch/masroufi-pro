package com.masroufi.pro.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.masroufi.pro.data.local.dao.AccountDao
import com.masroufi.pro.data.local.dao.CategoryDao
import com.masroufi.pro.data.local.dao.CurrencyRateDao
import com.masroufi.pro.data.local.dao.ReminderDao
import com.masroufi.pro.data.local.dao.TransactionDao
import com.masroufi.pro.data.local.database.MasroufiDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS reminders (
                id TEXT NOT NULL PRIMARY KEY,
                title TEXT NOT NULL DEFAULT '',
                hour INTEGER NOT NULL DEFAULT 20,
                minute INTEGER NOT NULL DEFAULT 0,
                isEnabled INTEGER NOT NULL DEFAULT 1,
                createdAt INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMasroufiDatabase(@ApplicationContext context: Context): MasroufiDatabase {
        return Room.databaseBuilder(
            context,
            MasroufiDatabase::class.java,
            "masroufi_database"
        )
        .addMigrations(MIGRATION_1_2)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: MasroufiDatabase): TransactionDao = database.transactionDao()

    @Provides
    @Singleton
    fun provideCategoryDao(database: MasroufiDatabase): CategoryDao = database.categoryDao()

    @Provides
    @Singleton
    fun provideAccountDao(database: MasroufiDatabase): AccountDao = database.accountDao()

    @Provides
    @Singleton
    fun provideCurrencyRateDao(database: MasroufiDatabase): CurrencyRateDao = database.currencyRateDao()

    @Provides
    @Singleton
    fun provideReminderDao(database: MasroufiDatabase): ReminderDao = database.reminderDao()
}
