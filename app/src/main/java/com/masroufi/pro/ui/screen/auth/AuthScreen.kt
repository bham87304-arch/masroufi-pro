package com.masroufi.pro.ui.screen.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masroufi.pro.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Navigate on auth success
    LaunchedEffect(uiState.isSignedIn) {
        if (uiState.isSignedIn) {
            onAuthSuccess?.invoke()
        }
    }

    // Show error in snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Show forgot password success
    LaunchedEffect(uiState.forgotPasswordSent) {
        if (uiState.forgotPasswordSent) {
            snackbarHostState.showSnackbar("Password reset email sent! Check your inbox.")
            viewModel.dismissForgotPasswordSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            uiState.isSignedIn -> {
                SignedInContent(
                    email = uiState.userEmail ?: "",
                    onSignOut = { viewModel.signOut() },
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.showOtpScreen -> {
                OtpVerificationContent(
                    email = uiState.otpEmail,
                    otpCode = uiState.otpCode,
                    isLoading = uiState.isLoading,
                    onOtpChange = { viewModel.setOtpCode(it) },
                    onVerify = { viewModel.verifyOtp() },
                    onResend = { viewModel.resendOtp() },
                    onBack = { viewModel.backFromOtp() },
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                AuthFormContent(
                    uiState = uiState,
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityChange = { passwordVisible = it },
                    onEmailChange = { viewModel.setEmail(it) },
                    onPasswordChange = { viewModel.setPassword(it) },
                    onSignIn = { viewModel.signIn() },
                    onSignUp = { viewModel.signUp() },
                    onToggleMode = { viewModel.toggleMode() },
                    onForgotPassword = { viewModel.forgotPassword() },
                    focusManager = focusManager,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun AuthFormContent(
    uiState: AuthUiState,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onToggleMode: () -> Unit,
    onForgotPassword: () -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "💰", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (uiState.isSignUp) stringResource(R.string.create_account)
                   else stringResource(R.string.sign_in_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email field
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.email)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password field
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.password)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                    Icon(
                        if (passwordVisible) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (uiState.isSignUp) onSignUp() else onSignIn()
                }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        // Forgot password
        if (!uiState.isSignUp) {
            TextButton(
                onClick = onForgotPassword,
                modifier = Modifier.align(Alignment.End),
                enabled = !uiState.isLoading
            ) {
                Text(
                    stringResource(R.string.forgot_password),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sign In / Sign Up button
        Button(
            onClick = { if (uiState.isSignUp) onSignUp() else onSignIn() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    if (uiState.isSignUp) stringResource(R.string.sign_up)
                    else stringResource(R.string.sign_in)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle sign in/up
        TextButton(
            onClick = onToggleMode,
            enabled = !uiState.isLoading
        ) {
            Text(
                if (uiState.isSignUp) stringResource(R.string.already_have_account)
                else stringResource(R.string.no_account_sign_up)
            )
        }
    }
}

@Composable
private fun OtpVerificationContent(
    email: String,
    otpCode: String,
    isLoading: Boolean,
    onOtpChange: (String) -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "📧", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.verify_email),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.otp_sent_to, email),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // OTP input field
        OutlinedTextField(
            value = otpCode,
            onValueChange = onOtpChange,
            label = { Text(stringResource(R.string.verification_code)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onVerify() }
            ),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 24.sp,
                letterSpacing = 8.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Verify button
        Button(
            onClick = onVerify,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading && otpCode.length == 6
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(R.string.verify))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resend code
        TextButton(
            onClick = onResend,
            enabled = !isLoading
        ) {
            Text(stringResource(R.string.resend_code))
        }
    }
}

@Composable
private fun SignedInContent(
    email: String,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✅", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(R.string.signed_in_as),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = email,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.data_synced),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.sign_out))
        }
    }
}
