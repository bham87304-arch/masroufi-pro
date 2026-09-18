package com.masroufi.pro.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.preferences.UserPreferencesManager
import com.masroufi.pro.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val currency: String = "DZD",
    val themeMode: String = "system",
    val language: String = "en",
    val isReminderEnabled: Boolean = false,
    val reminderTime: String = "20:00",
    val showCurrencyDialog: Boolean = false,
    val showThemeDialog: Boolean = false,
    val showLanguageDialog: Boolean = false,
    val showClearDataDialog: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesManager.userPreferencesFlow.collect { prefs ->
                _uiState.update {
                    it.copy(
                        currency = prefs.defaultCurrency,
                        themeMode = prefs.themeMode,
                        language = prefs.language,
                        isReminderEnabled = prefs.reminderEnabled,
                        reminderTime = prefs.reminderTime
                    )
                }
            }
        }
    }

    fun setCurrency(currencyCode: String) {
        viewModelScope.launch {
            userPreferencesManager.setDefaultCurrency(currencyCode)
            hideCurrencyDialog()
        }
    }

    fun setTheme(themeMode: String) {
        viewModelScope.launch {
            userPreferencesManager.setThemeMode(themeMode)
            hideThemeDialog()
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            userPreferencesManager.setLanguage(language)
            hideLanguageDialog()
        }
    }

    fun toggleReminder(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setReminderEnabled(enabled)
            if (enabled) {
                reminderScheduler.scheduleReminder(_uiState.value.reminderTime)
            } else {
                reminderScheduler.cancelReminder()
            }
        }
    }

    fun setReminderTime(time: String) {
        viewModelScope.launch {
            userPreferencesManager.setReminderTime(time)
            _uiState.update { it.copy(reminderTime = time) }
            if (_uiState.value.isReminderEnabled) {
                reminderScheduler.scheduleReminder(time)
            }
        }
    }

    fun clearData() {
        hideClearDataDialog()
    }

    fun showCurrencyDialog() { _uiState.update { it.copy(showCurrencyDialog = true) } }
    fun hideCurrencyDialog() { _uiState.update { it.copy(showCurrencyDialog = false) } }
    fun showThemeDialog() { _uiState.update { it.copy(showThemeDialog = true) } }
    fun hideThemeDialog() { _uiState.update { it.copy(showThemeDialog = false) } }
    fun showLanguageDialog() { _uiState.update { it.copy(showLanguageDialog = true) } }
    fun hideLanguageDialog() { _uiState.update { it.copy(showLanguageDialog = false) } }
    fun showClearDataDialog() { _uiState.update { it.copy(showClearDataDialog = true) } }
    fun hideClearDataDialog() { _uiState.update { it.copy(showClearDataDialog = false) } }
}
