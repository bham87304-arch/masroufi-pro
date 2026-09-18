package com.masroufi.pro

import android.app.Application
import android.content.Context
import com.masroufi.pro.data.local.dao.ReminderDao
import com.masroufi.pro.data.local.database.DatabaseSeeder
import com.masroufi.pro.notification.ReminderScheduler
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
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var reminderDao: ReminderDao
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            databaseSeeder.seedDefaultCategories()
            databaseSeeder.seedDefaultAccount()
            
            // Re-schedule all enabled reminders on app launch
            // Cancel first to avoid duplicate alarms
            try {
                val enabledReminders = reminderDao.getEnabledReminders()
                enabledReminders.forEach { reminder ->
                    // Cancel existing alarm first, then re-schedule
                    reminderScheduler.cancelReminder(reminder.id)
                    reminderScheduler.scheduleReminder(
                        reminder.id, reminder.hour, reminder.minute, reminder.title
                    )
                    // Save time to SharedPrefs for receiver rescheduling
                    val timeStr = String.format("%02d:%02d", reminder.hour, reminder.minute)
                    getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("time_${reminder.id}", timeStr)
                        .apply()
                }
            } catch (_: Exception) {
                // Ignore if database not yet available
            }
        }
    }
}
