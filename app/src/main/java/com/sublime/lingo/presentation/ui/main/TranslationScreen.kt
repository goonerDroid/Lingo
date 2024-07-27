package com.sublime.lingo.presentation.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sublime.lingo.presentation.ui.viewmodel.TranslationViewModel

@Composable
fun TranslationScreen(viewModel: TranslationViewModel = hiltViewModel()) {
    var sourceText by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }
    var sourceLanguage by remember { mutableStateOf("en") } // Default to English
    var targetLanguage by remember { mutableStateOf("id") } // Default
    // Observe the translation result
    LaunchedEffect(viewModel) {
        viewModel.translationResult.collect { result ->
            result?.let {
                translatedText = it
            }
        }
    }
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
    ) {
        TopBar()
        Spacer(modifier = Modifier.height(32.dp))
        LanguageSelector(
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            onSwapLanguages = {
                val temp = sourceLanguage
                sourceLanguage = targetLanguage
                targetLanguage = temp
                // Retranslate with swapped languages
                viewModel.translate(sourceText, sourceLanguage, targetLanguage)
            },
            onSourceLanguageChange = { sourceLanguage = it },
            onTargetLanguageChange = { targetLanguage = it },
        )
        Spacer(modifier = Modifier.height(16.dp))
        TranslationArea(
            sourceText = sourceText,
            onSourceTextChange = {
                sourceText = it
                viewModel.translate(it, sourceLanguage, targetLanguage)
            },
            translatedText = translatedText,
        )
    }
}

@Composable
fun TopBar() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
    ) {
        Text(
            text = "LinGO",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
        )
    }
}

@Composable
fun LanguageSelector(
    sourceLanguage: String,
    targetLanguage: String,
    onSwapLanguages: () -> Unit,
    onSourceLanguageChange: (String) -> Unit,
    onTargetLanguageChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        LanguageButton(
            flagResId = getFlagResource(sourceLanguage),
            languageName = getLanguageName(sourceLanguage),
            modifier = Modifier.weight(0.35f),
            onClick = { onSourceLanguageChange(sourceLanguage) },
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSwapLanguages,
            modifier =
                Modifier
                    .weight(0.2f)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Swap languages",
                tint = Color.White,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        LanguageButton(
            flagResId = getFlagResource(targetLanguage),
            languageName = getLanguageName(targetLanguage),
            modifier = Modifier.weight(0.35f),
            onClick = { onTargetLanguageChange(targetLanguage) },
        )
    }
}

@Composable
fun LanguageButton(
    flagResId: Int,
    languageName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
        modifier = modifier.wrapContentHeight(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.wrapContentSize(),
        ) {
            Text(
                text = languageName,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun TranslationArea(
    sourceText: String,
    onSourceTextChange: (String) -> Unit,
    translatedText: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = sourceText,
            onValueChange = onSourceTextChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            placeholder = { Text("Enter text") },
            colors =
                TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                ),
        )
        Divider()
        Text(
            text = translatedText,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(top = 8.dp),
        )
    }
}

// Helper functions (implement these based on your app's requirements)
fun getFlagResource(languageCode: String): Int {
    // Return the appropriate flag resource based on the language code
    return android.R.drawable.ic_menu_mylocation // Placeholder
}

fun getLanguageName(languageCode: String): String {
    // Return the full language name based on the language code
    return when (languageCode) {
        "en" -> "English"
        "id" -> "Indonesian"
        else -> "Unknown"
    }
}
