package com.sublime.lingo.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                ) {
                    AnimatedVisibility(
                        visible = showCustomSplashScreen && uiState.modelStatus == ModelStatus.LOADING,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        LingoSplashScreen(isLoading = true)
                    }

                    AnimatedVisibility(
                        visible = !showCustomSplashScreen || uiState.modelStatus != ModelStatus.LOADING,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        TranslationApp(viewModel)
                    }
                }

                LaunchedEffect(Unit) {
                    viewModel.fetchModelStatus()

                    keepSplashScreenOn = false

                    // The custom splash screen will be shown while the model is loading
                    while (uiState.modelStatus == ModelStatus.LOADING) {
                        delay(100) // Check status periodically
                    }
                    // Add a small delay before hiding the custom splash screen
                    delay(200)
                    showCustomSplashScreen = false
                }
            }
        }
    }
}
