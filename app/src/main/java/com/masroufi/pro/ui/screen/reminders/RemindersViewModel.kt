package com.masroufi.pro.ui.screen.reminders

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.local.dao.ReminderDao
import com.masroufi.pro.data.local.entity.ReminderEntity
import com.masroufi.pro.notification.ReminderWorker
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
            ReminderWorker.schedule(appContext, reminder.id, hour, minute, title)
        }
    }

    fun toggleReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            val updated = reminder.copy(isEnabled = !reminder.isEnabled)
            reminderDao.updateReminder(updated)
            if (updated.isEnabled) {
                ReminderWorker.schedule(appContext, updated.id, updated.hour, updated.minute, updated.title)
            } else {
                ReminderWorker.cancel(appContext, updated.id)
            }
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            ReminderWorker.cancel(appContext, reminder.id)
            reminderDao.deleteReminder(reminder)
        }
    }
}
