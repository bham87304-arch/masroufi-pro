package com.masroufi.pro.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.masroufi.pro.MainActivity
import com.masroufi.pro.R
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "masroufi_reminder"
        private const val TAG = "ReminderWorker"
        const val KEY_REMINDER_ID = "reminder_id"
        const val KEY_REMINDER_TITLE = "reminder_title"
        const val KEY_REMINDER_HOUR = "reminder_hour"
        const val KEY_REMINDER_MINUTE = "reminder_minute"

        fun schedule(context: Context, reminderId: String, hour: Int, minute: Int, title: String) {
            val delay = calculateDelay(hour, minute)
            Log.d(TAG, "Scheduling reminder '$reminderId' in ${delay / 60000} minutes (${hour}:${String.format("%02d", minute)})")

            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf(
                    KEY_REMINDER_ID to reminderId,
                    KEY_REMINDER_TITLE to title,
                    KEY_REMINDER_HOUR to hour,
                    KEY_REMINDER_MINUTE to minute
                ))
                .addTag("reminder_$reminderId")
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "reminder_$reminderId",
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
        }

        fun cancel(context: Context, reminderId: String) {
            WorkManager.getInstance(context)
                .cancelUniqueWork("reminder_$reminderId")
            Log.d(TAG, "Cancelled reminder $reminderId")
        }

        private fun calculateDelay(hour: Int, minute: Int): Long {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= now.timeInMillis) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            return target.timeInMillis - now.timeInMillis
        }
    }

    override suspend fun doWork(): Result {
        val reminderId = inputData.getString(KEY_REMINDER_ID) ?: return Result.failure()
        val title = inputData.getString(KEY_REMINDER_TITLE) ?: ""
        val hour = inputData.getInt(KEY_REMINDER_HOUR, 20)
        val minute = inputData.getInt(KEY_REMINDER_MINUTE, 0)

        Log.d(TAG, "Worker triggered for reminder: $reminderId")

        // Show notification
        showNotification(reminderId, title)

        // Re-schedule for tomorrow
        schedule(context, reminderId, hour, minute, title)

        return Result.success()
    }

    private fun showNotification(reminderId: String, title: String) {
        createNotificationChannel()

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

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentText = if (title.isNotBlank()) {
            title
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
        Log.d(TAG, "Notification displayed for: $reminderId")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Reminder"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = "Reminds you to track your expenses"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
