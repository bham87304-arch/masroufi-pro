package com.masroufi.pro

import android.app.Application
import android.util.Log
import com.masroufi.pro.data.local.dao.ReminderDao
import com.masroufi.pro.data.local.database.DatabaseSeeder
import com.masroufi.pro.data.sync.SyncManager
import com.masroufi.pro.notification.ReminderWorker
import dagger.hilt.android.HiltAndroidApp
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
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

    @Inject
    lateinit var syncManager: SyncManager

    @Inject
    lateinit var supabase: SupabaseClient
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            databaseSeeder.seedDefaultCategories()
            databaseSeeder.seedDefaultAccount()
            
            // Re-schedule all enabled reminders via WorkManager
            try {
                val enabledReminders = reminderDao.getEnabledReminders()
                enabledReminders.forEach { reminder ->
                    ReminderWorker.schedule(
                        this@MasroufiApplication,
                        reminder.id, reminder.hour, reminder.minute, reminder.title
                    )
                }
            } catch (_: Exception) { }

            // Auto-sync if signed in
            try {
                val user = supabase.auth.currentUserOrNull()
                if (user != null) {
                    Log.d("MasroufiApp", "User signed in, starting sync...")
                    val result = syncManager.fullSync()
                    Log.d("MasroufiApp", "Sync result: pushed=${result.pushed}, pulled=${result.pulled}")
                }
            } catch (e: Exception) {
                Log.w("MasroufiApp", "Auto-sync failed: ${e.message}")
            }
        }
    }
}
