package com.sublime.lingo.presentation.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
    var sourceLanguage by rememberSaveable { mutableStateOf("en") }
    var targetLanguage by rememberSaveable { mutableStateOf("id") }
    var sourceText by rememberSaveable { mutableStateOf("") }

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

    // Only update the state if the selected language is different
    LaunchedEffect(selectedLanguage?.value, languageType?.value) {
        selectedLanguage?.value?.let { language ->
            when (languageType?.value) {
                "source" -> {
                    if (sourceLanguage != language) {
                        sourceLanguage = language
                        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selectedLanguage")
                        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("languageType")
                    }
                }

                "target" -> {
                    if (targetLanguage != language) {
                        targetLanguage = language
                        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selectedLanguage")
                        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("languageType")
                    }
                }
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
        )
        Spacer(modifier = Modifier.height(16.dp))
        TranslationArea(
            sourceText = sourceText,
            onSourceTextChange = {
                sourceText = it
                viewModel.translate(it, sourceLanguage, targetLanguage)
            },
            translatedText = "", // TODO Check this later
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                TextFieldDefaults.textFieldColors(
                    Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
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

@Composable
fun LanguageSelector(
    sourceLanguage: String,
    targetLanguage: String,
    onSwapLanguages: () -> Unit,
    onSourceLanguageChange: () -> Unit,
    onTargetLanguageChange: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        LanguageButton(languageItem = sourceLanguage, onClick = onSourceLanguageChange)
        IconButton(
            onClick = onSwapLanguages,
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Swap languages",
                tint = Color.White,
            )
        }
        LanguageButton(languageItem = targetLanguage, onClick = onTargetLanguageChange)
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
                painter = painterResource(id = getFlagResource(languageItem)),
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
            text = "LinGO",
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
                .background(Color.White)
                .padding(16.dp),
    ) {
        TopBar()
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            items(getSupportedLanguages()) { language ->
                LanguageListItem(
                    languageCode = language,
                    onClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selectedLanguage", language)
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
            painter = painterResource(id = getFlagResource(languageCode)),
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

// Helper functions
fun getSupportedLanguages(): List<String> {
    return listOf("en", "id", "fr", "es", "de") // Add more language codes as needed
}

fun getFlagResource(languageCode: String): Int {
    // Return the appropriate flag resource based on the language code
    return when (languageCode) {
        "en" -> R.drawable.ic_launcher_foreground
        "id" -> R.drawable.ic_launcher_foreground
        "fr" -> R.drawable.ic_launcher_foreground
        "es" -> R.drawable.ic_launcher_foreground
        else -> R.drawable.ic_launcher_foreground
    }
}

fun getLanguageName(languageCode: String): String =
    when (languageCode) {
        "en" -> "English"
        "id" -> "Indonesian"
        "fr" -> "French"
        "es" -> "Spanish"
        "de" -> "German"
        else -> "Unknown"
    }
