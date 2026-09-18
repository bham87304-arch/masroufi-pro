package com.masroufi.pro

import android.app.Application
import android.content.Context
import com.masroufi.pro.data.local.database.DatabaseSeeder
import com.masroufi.pro.data.preferences.UserPreferencesManager
import com.masroufi.pro.notification.ReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MasroufiApplication : Application() {
    
    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    @Inject
    lateinit var userPreferencesManager: UserPreferencesManager

    @Inject
    lateinit var reminderScheduler: ReminderScheduler
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            databaseSeeder.seedDefaultCategories()
            databaseSeeder.seedDefaultAccount()
            
            // Re-schedule reminder on every app launch (alarms are lost on reinstall/reboot)
            try {
                val prefs = userPreferencesManager.userPreferencesFlow.first()
                if (prefs.reminderEnabled) {
                    reminderScheduler.scheduleReminder(prefs.reminderTime)
                    // Sync to SharedPrefs so BroadcastReceiver can read for rescheduling
                    getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("reminder_enabled", true)
                        .putString("reminder_time", prefs.reminderTime)
                        .apply()
                }
            } catch (_: Exception) {
                // Ignore if preferences not yet available
            }
        }
    }
}
