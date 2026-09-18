package com.masroufi.pro.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class UserPreferences(
    val defaultCurrency: String = "DZD",
    val themeMode: String = "system",
    val language: String = "auto",
    val isFirstLaunch: Boolean = true,
    val reminderEnabled: Boolean = false,
    val reminderTime: String = "20:00"
)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val DEFAULT_CURRENCY = stringPreferencesKey("default_currency")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
        UserPreferences(
            defaultCurrency = preferences[DEFAULT_CURRENCY] ?: "DZD",
            themeMode = preferences[THEME_MODE] ?: "system",
            language = preferences[LANGUAGE] ?: "auto",
            isFirstLaunch = preferences[IS_FIRST_LAUNCH] ?: true,
            reminderEnabled = preferences[REMINDER_ENABLED] ?: false,
            reminderTime = preferences[REMINDER_TIME] ?: "20:00"
        )
    }

    suspend fun setDefaultCurrency(currency: String) {
        dataStore.edit { it[DEFAULT_CURRENCY] = currency }
    }

    suspend fun setThemeMode(theme: String) {
        dataStore.edit { it[THEME_MODE] = theme }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { it[LANGUAGE] = language }
    }

    suspend fun setFirstLaunch(isFirstLaunch: Boolean) {
        dataStore.edit { it[IS_FIRST_LAUNCH] = isFirstLaunch }
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[REMINDER_ENABLED] = enabled }
    }

    suspend fun setReminderTime(time: String) {
        dataStore.edit { it[REMINDER_TIME] = time }
    }
}
