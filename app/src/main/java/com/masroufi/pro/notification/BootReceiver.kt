package com.masroufi.pro.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.masroufi.pro.data.local.dao.ReminderDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderDao: ReminderDao

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device booted, rescheduling reminders via WorkManager")
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val enabledReminders = reminderDao.getEnabledReminders()
                    enabledReminders.forEach { reminder ->
                        ReminderWorker.schedule(
                            context, reminder.id, reminder.hour, reminder.minute, reminder.title
                        )
                    }
                    Log.d("BootReceiver", "Rescheduled ${enabledReminders.size} reminders")
                } catch (e: Exception) {
                    Log.w("BootReceiver", "Failed to reschedule: ${e.message}")
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
