package com.sublime.lingo.presentation.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sublime.lingo.R
import com.sublime.lingo.presentation.ui.getFlagResource
import com.sublime.lingo.presentation.ui.getLanguageName
import com.sublime.lingo.presentation.ui.getSupportedLanguages
import com.sublime.lingo.presentation.ui.viewmodel.TranslationViewModel

@Suppress("ktlint:standard:function-naming")
@Composable
fun TranslationApp(viewModel: TranslationViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val sourceLanguage by viewModel.sourceLanguage.collectAsState()
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()

    NavHost(navController, startDestination = "translation") {
        composable("translation") {
            ChatTranslationScreen(
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                chatMessages = chatMessages,
                inputText = inputText,
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
fun AnimatedSwapLanguageButton(
    onSwapLanguages: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isRotated by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isRotated) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "rotationAnimation",
    )

    IconButton(
        onClick = {
            isRotated = !isRotated
            onSwapLanguages()
        },
        modifier =
            modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
    ) {
        Image(
            painter = painterResource(id = R.drawable.sync_24dp),
            contentDescription = "Swap languages",
            modifier =
                Modifier
                    .size(24.dp)
                    .rotate(rotationAngle),
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun LanguageSelector(
    sourceLanguage: String,
    targetLanguage: String,
    onSwapLanguages: () -> Unit,
    onSourceLanguageChange: () -> Unit,
    onTargetLanguageChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        LanguageButton(
            languageItem = sourceLanguage,
            onClick = onSourceLanguageChange,
        )
        AnimatedSwapLanguageButton(onSwapLanguages = onSwapLanguages)
        LanguageButton(
            languageItem = targetLanguage,
            onClick = onTargetLanguageChange,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun LanguageButton(
    languageItem: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Color.LightGray),
        modifier =
            modifier
                .padding(horizontal = 8.dp)
                .height(50.dp)
                .width(135.dp)
                .clip(CircleShape),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Image(
                painter = painterResource(id = getFlagResource(LocalContext.current, languageItem)),
                contentDescription = "Flag",
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = getLanguageName(languageItem),
                color = Color.Black,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun TopBar(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
    ) {
        Text(
            text = "Lingo",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun LanguageSelectionScreen(
    languageType: String?,
    sourceLanguage: String,
    targetLanguage: String,
    onLanguageSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val availableLanguages =
        remember(sourceLanguage, targetLanguage, languageType) {
            getSupportedLanguages().filter { (_, code) ->
                when (languageType) {
                    "source" -> code != sourceLanguage && code != targetLanguage
                    "target" -> code != sourceLanguage && code != targetLanguage
                    else -> true
                }
            }
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        TopBar()
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
        ) {
            items(availableLanguages) { language ->
                LanguageListItem(
                    languageCode = language.second,
                    onClick = { onLanguageSelect(language.second) },
                )
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun LanguageListItem(
    languageCode: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSelected by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        if (isSelected) Color.LightGray else Color.Transparent,
        label = "backgroundColorAnimation",
    )

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .clickable {
                    isSelected = true
                    onClick()
                }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = getFlagResource(LocalContext.current, languageCode)),
            contentDescription = "Flag",
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = getLanguageName(languageCode),
            fontSize = 18.sp,
        )
    }
}
