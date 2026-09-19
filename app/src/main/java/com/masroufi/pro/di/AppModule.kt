package com.masroufi.pro.di

import android.content.Context
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // ReminderScheduler removed - using WorkManager (ReminderWorker) instead
}
