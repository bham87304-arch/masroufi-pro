package com.masroufi.pro.ui.screen.reminders

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.local.dao.ReminderDao
import com.masroufi.pro.data.local.entity.ReminderEntity
import com.masroufi.pro.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val reminderDao: ReminderDao,
    private val reminderScheduler: ReminderScheduler,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    val reminders: StateFlow<List<ReminderEntity>> = reminderDao.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReminder(hour: Int, minute: Int, title: String) {
        viewModelScope.launch {
            val reminder = ReminderEntity(
                title = title,
                hour = hour,
                minute = minute,
                isEnabled = true
            )
            reminderDao.insertReminder(reminder)
            scheduleAlarm(reminder)
        }
    }

    fun toggleReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            val updated = reminder.copy(isEnabled = !reminder.isEnabled)
            reminderDao.updateReminder(updated)
            if (updated.isEnabled) {
                scheduleAlarm(updated)
            } else {
                reminderScheduler.cancelReminder(updated.id)
            }
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderScheduler.cancelReminder(reminder.id)
            reminderDao.deleteReminder(reminder)
        }
    }

    private fun scheduleAlarm(reminder: ReminderEntity) {
        reminderScheduler.scheduleReminder(reminder.id, reminder.hour, reminder.minute, reminder.title)
        // Save time to SharedPrefs for receiver rescheduling
        val timeStr = String.format("%02d:%02d", reminder.hour, reminder.minute)
        appContext.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("time_${reminder.id}", timeStr)
            .apply()
    }

    fun rescheduleAllEnabled() {
        viewModelScope.launch {
            val enabledReminders = reminderDao.getEnabledReminders()
            enabledReminders.forEach { scheduleAlarm(it) }
        }
    }
}
