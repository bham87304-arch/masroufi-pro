package com.masroufi.pro.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Legacy BroadcastReceiver kept for backward compatibility.
 * All reminder logic has been moved to ReminderWorker (WorkManager).
 */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("ReminderReceiver", "Legacy receiver triggered - notifications now handled by WorkManager")
    }
}
