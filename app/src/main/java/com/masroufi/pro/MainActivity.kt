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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    private var currentLanguage: String? = null

    override fun attachBaseContext(newBase: Context) {
        // Read the saved language synchronously from the DataStore preferences file
        // DataStore stores preferences in an XML file at: data/data/pkg/files/datastore/user_preferences.preferences_pb
        // We can't read protobuf easily, so use a SharedPreferences fallback
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

            // When language changes, save to SharedPrefs (for attachBaseContext) and recreate
            LaunchedEffect(preferences.language) {
                val prefs = getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
                val savedLang = prefs.getString("app_language", "en")
                if (currentLanguage == null) {
                    currentLanguage = preferences.language
                } else if (preferences.language != currentLanguage) {
                    prefs.edit().putString("app_language", preferences.language).apply()
                    currentLanguage = preferences.language
                    recreate()
                    return@LaunchedEffect
                }
                // Also save on first load in case it wasn't saved yet
                if (savedLang != preferences.language) {
                    prefs.edit().putString("app_language", preferences.language).apply()
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
