package com.masroufi.pro.ui.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masroufi.pro.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isSignUp: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSignedIn: Boolean = false,
    val userEmail: String? = null,
    val showForgotPassword: Boolean = false,
    val forgotPasswordSent: Boolean = false,
    val signUpSuccess: Boolean = false,
    // OTP verification
    val showOtpScreen: Boolean = false,
    val otpCode: String = "",
    val otpEmail: String = "",
    val otpVerified: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val supabase: SupabaseClient,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "AuthViewModel"
    }

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val user = supabase.auth.currentUserOrNull()
                        _uiState.update {
                            it.copy(
                                isSignedIn = true,
                                isLoading = false,
                                userEmail = user?.email,
                                error = null,
                                showOtpScreen = false
                            )
                        }
                        Log.d(TAG, "Authenticated: ${user?.email}")
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _uiState.update {
                            it.copy(
                                isSignedIn = false,
                                isLoading = false,
                                userEmail = null
                            )
                        }
                        Log.d(TAG, "Not authenticated")
                    }
                    is SessionStatus.Initializing -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is SessionStatus.RefreshFailure -> {
                        _uiState.update {
                            it.copy(
                                isSignedIn = false,
                                isLoading = false,
                                error = "Session expired. Please sign in again."
                            )
                        }
                        Log.d(TAG, "Refresh failure")
                    }
                }
            }
        }
    }

    fun setEmail(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun setPassword(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun setOtpCode(code: String) {
        // Only allow digits, max 6
        val filtered = code.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(otpCode = filtered, error = null) }
    }

    fun toggleMode() {
        _uiState.update {
            it.copy(
                isSignUp = !it.isSignUp,
                error = null,
                signUpSuccess = false,
                showOtpScreen = false
            )
        }
    }

    fun signIn() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all fields") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                supabase.auth.signInWith(Email) {
                    email = state.email.trim()
                    password = state.password
                }
                Log.d(TAG, "Sign in successful")
            } catch (e: Exception) {
                Log.e(TAG, "Sign in failed", e)
                val errorMsg = when {
                    e.message?.contains("Invalid login", true) == true -> "Invalid email or password"
                    e.message?.contains("Email not confirmed", true) == true -> "Please verify your email first"
                    e.message?.contains("network", true) == true -> "Network error. Please try again."
                    else -> e.message ?: "Sign in failed"
                }
                _uiState.update { it.copy(isLoading = false, error = errorMsg) }
            }
        }
    }

    fun signUp() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all fields") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                supabase.auth.signUpWith(Email) {
                    email = state.email.trim()
                    password = state.password
                }
                // Show OTP verification screen
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showOtpScreen = true,
                        otpEmail = state.email.trim(),
                        otpCode = "",
                        error = null
                    )
                }
                Log.d(TAG, "Sign up successful, showing OTP screen")
            } catch (e: Exception) {
                Log.e(TAG, "Sign up failed", e)
                val errorMsg = when {
                    e.message?.contains("already registered", true) == true -> "This email is already registered"
                    e.message?.contains("valid email", true) == true -> "Please enter a valid email"
                    e.message?.contains("network", true) == true -> "Network error. Please try again."
                    else -> e.message ?: "Sign up failed"
                }
                _uiState.update { it.copy(isLoading = false, error = errorMsg) }
            }
        }
    }

    fun verifyOtp() {
        val state = _uiState.value
        if (state.otpCode.length != 6) {
            _uiState.update { it.copy(error = "Please enter the 6-digit code") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                supabase.auth.verifyEmailOtp(
                    type = OtpType.Email.SIGNUP,
                    email = state.otpEmail,
                    token = state.otpCode
                )
                Log.d(TAG, "OTP verified successfully")
                // Auth state will be updated by observeAuthState -> Authenticated
            } catch (e: Exception) {
                Log.e(TAG, "OTP verification failed", e)
                val errorMsg = when {
                    e.message?.contains("expired", true) == true -> "Code expired. Request a new one."
                    e.message?.contains("invalid", true) == true -> "Invalid code. Please try again."
                    else -> e.message ?: "Verification failed"
                }
                _uiState.update { it.copy(isLoading = false, error = errorMsg) }
            }
        }
    }

    fun resendOtp() {
        val email = _uiState.value.otpEmail
        if (email.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                supabase.auth.resendEmail(OtpType.Email.SIGNUP, email)
                _uiState.update {
                    it.copy(isLoading = false, error = null, otpCode = "")
                }
                Log.d(TAG, "OTP resent to $email")
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to resend code")
                }
            }
        }
    }

    fun backFromOtp() {
        _uiState.update {
            it.copy(showOtpScreen = false, otpCode = "", error = null)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                _uiState.update {
                    it.copy(
                        isSignedIn = false,
                        userEmail = null,
                        email = "",
                        password = ""
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sign out failed", e)
            }
        }
    }

    fun forgotPassword() {
        val email = _uiState.value.email
        if (email.isBlank()) {
            _uiState.update { it.copy(error = "Please enter your email first") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                supabase.auth.resetPasswordForEmail(email.trim())
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        forgotPasswordSent = true,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to send reset email")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun dismissForgotPasswordSuccess() {
        _uiState.update { it.copy(forgotPasswordSent = false) }
    }
}
