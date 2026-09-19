package com.masroufi.pro

import android.app.Application
import com.masroufi.pro.data.local.dao.ReminderDao
import com.masroufi.pro.data.local.database.DatabaseSeeder
import com.masroufi.pro.notification.ReminderWorker
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

    @Inject
    lateinit var reminderDao: ReminderDao
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            databaseSeeder.seedDefaultCategories()
            databaseSeeder.seedDefaultAccount()
            
            // Re-schedule all enabled reminders via WorkManager
            // WorkManager handles dedup via REPLACE policy
            try {
                val enabledReminders = reminderDao.getEnabledReminders()
                enabledReminders.forEach { reminder ->
                    ReminderWorker.schedule(
                        this@MasroufiApplication,
                        reminder.id, reminder.hour, reminder.minute, reminder.title
                    )
                }
            } catch (_: Exception) {
                // Ignore if database not yet available
            }
        }
    }
}
