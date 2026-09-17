package com.masroufi.pro.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for application-level dependencies.
 *
 * Note: Repositories (TransactionRepository, CategoryRepository, AccountRepository,
 * CurrencyRepository), DatabaseSeeder, and UserPreferencesManager all have @Inject
 * constructors, so Hilt can provide them automatically without @Provides methods.
 *
 * This module is reserved for bindings that require manual configuration,
 * such as Supabase client setup (to be added later).
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Supabase client will be provided here once configured
    // @Provides @Singleton fun provideSupabaseClient(): SupabaseClient { ... }
}
