package com.masroufi.pro.ui.screen.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isSignUp: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSignedIn: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    // private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun setEmail(email: String) {}
    fun setPassword(password: String) {}
    fun toggleMode() {}
    fun signIn() {}
    fun signUp() {}
    fun signOut() {}
    fun forgotPassword() {}
}
