package com.sublime.lingo.presentation.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.sublime.lingo.R
import com.sublime.lingo.presentation.ui.getFlagResource
import com.sublime.lingo.presentation.ui.getLanguageName
import com.sublime.lingo.presentation.ui.getSupportedLanguages
import com.sublime.lingo.presentation.ui.viewmodel.TranslationState
import com.sublime.lingo.presentation.ui.viewmodel.TranslationViewModel

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun TranslationApp() {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)
    NavHost(navController, startDestination = "translation") {
        composable("translation") {
            TranslationScreen(navController)
        }
        composable("languageSelection/{languageType}") { backStackEntry ->
            val languageType = backStackEntry.arguments?.getString("languageType")
            LanguageSelectionScreen(navController, languageType)
        }
    }
}

@Composable
fun TranslationScreen(
    navController: NavHostController,
    viewModel: TranslationViewModel = hiltViewModel(),
) {
    val sourceLanguage by viewModel.sourceLanguage.collectAsState()
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val translationState by viewModel.translationResult.collectAsState()

    val selectedLanguage =
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("selectedLanguage")
            ?.observeAsState()
    val languageType =
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("languageType")
            ?.observeAsState()

    LaunchedEffect(selectedLanguage?.value, languageType?.value) {
        selectedLanguage?.value?.let { language ->
            when (languageType?.value) {
                "source" -> viewModel.setSourceLanguage(language)
                "target" -> viewModel.setTargetLanguage(language)
            }
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selectedLanguage")
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("languageType")
        }
    }

    TranslationScreenContent(
        sourceLanguage = sourceLanguage,
        targetLanguage = targetLanguage,
        inputText = inputText,
        translationState = translationState,
        onSwapLanguages = {
            val tempSource = sourceLanguage
            viewModel.setSourceLanguage(targetLanguage)
            viewModel.setTargetLanguage(tempSource)
        },
        onSourceLanguageChange = {
            navController.navigate("languageSelection/source") {
                launchSingleTop = true
                restoreState = true
            }
        },
        onTargetLanguageChange = {
            navController.navigate("languageSelection/target") {
                launchSingleTop = true
                restoreState = true
            }
        },
        onInputTextChange = viewModel::updateInputText,
        onTranslate = viewModel::translate,
    )
}

@Composable
fun TranslationScreenContent(
    sourceLanguage: String,
    targetLanguage: String,
    inputText: String,
    translationState: TranslationState,
    onSwapLanguages: () -> Unit,
    onSourceLanguageChange: () -> Unit,
    onTargetLanguageChange: () -> Unit,
    onInputTextChange: (String) -> Unit,
    onTranslate: () -> Unit,
) {
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
            onSwapLanguages = onSwapLanguages,
            onSourceLanguageChange = onSourceLanguageChange,
            onTargetLanguageChange = onTargetLanguageChange,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TranslationArea(
            inputText = inputText,
            translationState = translationState,
            onInputTextChange = onInputTextChange,
            onTranslate = onTranslate,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TranslationArea(
    inputText: String,
    translationState: TranslationState,
    onInputTextChange: (String) -> Unit,
    onTranslate: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Input area
        BasicTextField(
            value = inputText,
            onValueChange = onInputTextChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            textStyle = TextStyle(fontSize = 18.sp),
            decorationBox = { innerTextField ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                ) {
                    if (inputText.isEmpty()) {
                        Text(
                            "Type the text to translate",
                            color = Color.Gray,
                            fontSize = 18.sp,
                        )
                    }
                    innerTextField()
                }
            },
        )

        // Translate button
        Button(
            onClick = onTranslate,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Text("Translate")
        }

        Divider(color = Color.LightGray, thickness = 1.dp)

        // Output area
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            when (translationState) {
                is TranslationState.Idle -> {
                    Text(
                        "Translation will appear here",
                        color = Color.Gray,
                        fontSize = 18.sp,
                    )
                }

                is TranslationState.Loading -> {
                    CircularProgressIndicator()
                }

                is TranslationState.Success -> {
                    Text(
                        text = translationState.translatedText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                is TranslationState.Error -> {
                    Text(
                        text = translationState.message,
                        color = Color.Red,
                        fontSize = 18.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedSwapLanguageButton(onSwapLanguages: () -> Unit) {
    var isRotated by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isRotated) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "",
    )

    IconButton(
        onClick = {
            isRotated = !isRotated
            onSwapLanguages()
        },
        modifier =
            Modifier
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

@Composable
fun LanguageSelector(
    sourceLanguage: String,
    targetLanguage: String,
    onSwapLanguages: () -> Unit,
    onSourceLanguageChange: () -> Unit,
    onTargetLanguageChange: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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

@Composable
fun LanguageButton(
    languageItem: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Color.LightGray),
        modifier =
            Modifier
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

@Composable
fun TopBar() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
    ) {
        Text(
            text = "Lingo",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun LanguageSelectionScreen(
    navController: NavHostController,
    languageType: String?,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        TopBar()
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            items(getSupportedLanguages()) { language ->
                LanguageListItem(
                    languageCode = language.second,
                    onClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selectedLanguage", language.second)
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("languageType", languageType)
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}

@Composable
fun LanguageListItem(
    languageCode: String,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
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
