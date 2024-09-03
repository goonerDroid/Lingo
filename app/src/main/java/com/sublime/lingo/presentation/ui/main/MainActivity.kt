package com.sublime.lingo.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sublime.lingo.presentation.ui.composables.LingoSplashScreen
import com.sublime.lingo.presentation.ui.composables.TranslationApp
import com.sublime.lingo.presentation.ui.theme.LingoTheme
import com.sublime.lingo.presentation.viewmodel.ModelStatus
import com.sublime.lingo.presentation.viewmodel.TranslationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: TranslationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        var keepSplashScreenOn by mutableStateOf(true)
        splashScreen.setKeepOnScreenCondition { keepSplashScreenOn }

        setContent {
            LingoTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var showCustomSplashScreen by remember { mutableStateOf(true) }
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        showCustomSplashScreen -> {
                            LingoSplashScreen(
                                isLoading = uiState.modelStatus == ModelStatus.LOADING,
                            )
                        }

                        else -> TranslationApp(viewModel)
                    }
                }

                LaunchedEffect(Unit) {
                    viewModel.fetchModelStatus()

                    // Wait for the initial model status update
                    viewModel.uiState.first { it.modelStatus != ModelStatus.LOADING }

                    // Delay to show the system splash screen for a minimum time
                    delay(500)
                    keepSplashScreenOn = false

                    // Additional delay for custom splash screen
                    delay(2000)
                    showCustomSplashScreen = false
                }
            }
        }
    }
}
