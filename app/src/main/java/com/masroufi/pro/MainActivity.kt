package com.masroufi.pro

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import com.masroufi.pro.data.preferences.UserPreferences
import com.masroufi.pro.data.preferences.UserPreferencesManager
import com.masroufi.pro.ui.navigation.MasroufiNavHost
import com.masroufi.pro.ui.theme.MasroufiProTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesManager: UserPreferencesManager

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or not, the alarm is already scheduled */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()

        setContent {
            val preferences by userPreferencesManager.userPreferencesFlow
                .collectAsState(initial = UserPreferences())

            // Create a context with the correct locale — no recreate() needed!
            val locale = Locale(preferences.language)
            Locale.setDefault(locale)
            val config = Configuration(resources.configuration).apply { setLocale(locale) }
            val localizedContext = createConfigurationContext(config)

            // RTL support for Arabic
            val layoutDirection = if (preferences.language == "ar") {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            val darkTheme = when (preferences.themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            // Provide the localized context so all stringResource() calls use the correct language
            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalLayoutDirection provides layoutDirection
            ) {
                MasroufiProTheme(darkTheme = darkTheme) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        MasroufiNavHost()
                    }
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
