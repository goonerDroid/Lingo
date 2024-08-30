package com.sublime.lingo.presentation.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sublime.lingo.presentation.ui.theme.DarkPurple
import com.sublime.lingo.presentation.ui.theme.Purple80
import com.sublime.lingo.presentation.viewmodel.ModelStatus
import com.sublime.lingo.presentation.viewmodel.TranslationViewModel

@Suppress("ktlint:standard:function-naming")
@Composable
fun TranslationApp(viewModel: TranslationViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val sourceLanguage by viewModel.sourceLanguage.collectAsState()
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(navController, startDestination = "translation") {
        composable("translation") {
            ChatTranslationScreen(
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                chatMessages = uiState.chatMessages,
                inputText = uiState.inputText,
                isTyping = uiState.isTyping,
                modelStatus = uiState.modelStatus,
                onInputTextChange = viewModel::updateInputText,
                onSendClick = viewModel::sendMessage,
                onSwapLanguages = viewModel::swapLanguages,
                onSourceLanguageChange = {
                    navController.navigate("languageSelection/source")
                },
                onTargetLanguageChange = {
                    navController.navigate("languageSelection/target")
                },
            )
        }
        composable("languageSelection/{languageType}") { backStackEntry ->
            val languageType = backStackEntry.arguments?.getString("languageType")
            LanguageSelectionScreen(
                languageType = languageType,
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                modelStatus = uiState.modelStatus,
                onLanguageSelect = { selectedLanguage ->
                    when (languageType) {
                        "source" -> viewModel.setSourceLanguage(selectedLanguage)
                        "target" -> viewModel.setTargetLanguage(selectedLanguage)
                    }
                    navController.popBackStack()
                },
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun TopBar(
    status: ModelStatus,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Purple80)
                .padding(vertical = 8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(
                text = "Lingo",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkPurple,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatusIndicator(status = status)
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun StatusIndicator(
    status: ModelStatus,
    modifier: Modifier = Modifier,
) {
    val color =
        when (status) {
            ModelStatus.READY -> Color.Green
            ModelStatus.LOADING -> Color(0xFFFFA500)
            ModelStatus.ERROR -> Color.Red
        }

    Box(
        modifier =
            modifier
                .size(12.dp)
                .background(color, CircleShape)
                .border(1.dp, Color.White, CircleShape),
    )
}
