package com.masroufi.pro

import android.app.Application
import com.masroufi.pro.data.local.database.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MasroufiApplication : Application() {
    
    @Inject
    lateinit var databaseSeeder: DatabaseSeeder
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            databaseSeeder.seedDefaultCategories()
            databaseSeeder.seedDefaultAccount()
        }
    }
}
