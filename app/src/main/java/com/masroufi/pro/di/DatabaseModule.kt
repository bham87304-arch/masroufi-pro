package com.masroufi.pro.di

import android.content.Context
import androidx.room.Room
import com.masroufi.pro.data.local.dao.AccountDao
import com.masroufi.pro.data.local.dao.CategoryDao
import com.masroufi.pro.data.local.dao.CurrencyRateDao
import com.masroufi.pro.data.local.dao.TransactionDao
import com.masroufi.pro.data.local.database.MasroufiDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
        ).build()
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
}
