package com.masroufi.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.masroufi.pro.ui.navigation.MasroufiNavHost
import com.masroufi.pro.ui.theme.MasroufiProTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MasroufiProTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MasroufiNavHost()
                }
            }
        }
    }
}
