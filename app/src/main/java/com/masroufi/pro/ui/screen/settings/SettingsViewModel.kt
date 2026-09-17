package com.masroufi.pro.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.preferences.UserPreferences
import com.masroufi.pro.data.preferences.UserPreferencesManager
import com.masroufi.pro.data.repository.AccountRepository
import com.masroufi.pro.data.repository.CategoryRepository
import com.masroufi.pro.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val preferences: UserPreferences = UserPreferences(),
    val exportStatus: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setCurrency(currency: String) {}
    fun setTheme(theme: String) {}
    fun setLanguage(language: String) {}
    fun toggleReminder(enabled: Boolean) {}
    fun setReminderTime(time: String) {}
    fun exportData() {}
    fun clearData() {}
}
