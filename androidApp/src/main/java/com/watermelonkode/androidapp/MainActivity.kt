package com.watermelonkode.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.watermelonkode.simpletemplate.ui.App
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            !AppInitializer.isReady
        }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        lifecycleScope.launch {
            AppInitializer.init()
        }

        setContent {
            App()
        }
    }
}

object AppInitializer {
    var isReady = false
        private set

    suspend fun init() {
        delay(1500)
        isReady = true
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}