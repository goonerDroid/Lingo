package com.sublime.lingo.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sublime.lingo.presentation.ui.composables.TranslationApp
import com.sublime.lingo.presentation.ui.theme.LingoTheme
import com.sublime.lingo.presentation.viewmodel.ModelStatus
import com.sublime.lingo.presentation.viewmodel.TranslationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: TranslationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            // Keep the splash screen on screen until the UI state is ready
            splashScreen.setKeepOnScreenCondition {
                uiState.modelStatus != ModelStatus.READY
            }

            LingoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    TranslationApp(viewModel)
                }
            }

            // Trigger the fetchModelStatus when the composable is first launched
            LaunchedEffect(Unit) {
                viewModel.fetchModelStatus()
            }
        }
    }
}
