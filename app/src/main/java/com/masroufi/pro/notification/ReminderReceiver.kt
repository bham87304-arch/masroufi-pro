package com.masroufi.pro.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.masroufi.pro.MainActivity
import com.masroufi.pro.R

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "masroufi_reminder"
        private const val TAG = "ReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "onReceive triggered!")
        
        val reminderId = intent?.getStringExtra("reminder_id") ?: "legacy"
        val reminderTitle = intent?.getStringExtra("reminder_title") ?: ""

        // Apply app locale
        val prefs = context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
        val savedLang = prefs.getString("app_language", "auto") ?: "auto"
        val lang = if (savedLang == "auto") {
            val deviceLang = java.util.Locale.getDefault().language
            if (deviceLang in listOf("en", "ar", "fr")) deviceLang else "en"
        } else {
            savedLang
        }
        val locale = java.util.Locale(lang)
        val config = android.content.res.Configuration(context.resources.configuration)
        config.setLocale(locale)
        val localizedContext = context.createConfigurationContext(config)

        createNotificationChannel(context)

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentText = if (reminderTitle.isNotBlank()) {
            reminderTitle
        } else {
            localizedContext.getString(R.string.daily_reminder_message)
        }

        val notificationId = reminderId.hashCode().and(0x7FFFFFFF)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(localizedContext.getString(R.string.app_name))
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
        Log.d(TAG, "Notification displayed for reminder: $reminderId")

        // Re-schedule for tomorrow
        rescheduleForTomorrow(context, reminderId, reminderTitle)
    }

    private fun rescheduleForTomorrow(context: Context, reminderId: String, title: String) {
        try {
            val reminderPrefs = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
            val time = reminderPrefs.getString("time_$reminderId", "20:00") ?: "20:00"
            val parts = time.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 20
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
            
            val scheduler = ReminderScheduler(context)
            scheduler.scheduleReminder(reminderId, hour, minute, title)
            Log.d(TAG, "Re-scheduled reminder $reminderId for tomorrow at $time")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to reschedule: ${e.message}")
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Reminder"
            val descriptionText = "Reminds you to track your expenses"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
