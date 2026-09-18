package com.masroufi.pro

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
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

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("app_language", "en") ?: "en"
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferences by userPreferencesManager.userPreferencesFlow
                .collectAsState(initial = UserPreferences())

            // Only recreate when user ACTIVELY changes language (not on initial load)
            // Compare DataStore language vs SharedPrefs language (which survives recreate)
            LaunchedEffect(preferences.language) {
                if (preferences.language.isBlank()) return@LaunchedEffect
                val prefs = getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
                val appliedLang = prefs.getString("app_language", "en") ?: "en"
                
                if (preferences.language != appliedLang) {
                    // User changed language in settings — save and recreate ONCE
                    prefs.edit().putString("app_language", preferences.language).apply()
                    recreate()
                }
            }

            val darkTheme = when (preferences.themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            MasroufiProTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MasroufiNavHost()
                }
            }
        }
    }
}
