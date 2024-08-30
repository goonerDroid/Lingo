package com.sublime.lingo.presentation.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sublime.lingo.R
import com.sublime.lingo.presentation.ui.getFlagResource
import com.sublime.lingo.presentation.ui.getLanguageName
import com.sublime.lingo.presentation.ui.getSupportedLanguages
import com.sublime.lingo.presentation.ui.theme.Purple40
import com.sublime.lingo.presentation.ui.theme.Purple80
import com.sublime.lingo.presentation.ui.theme.PurpleGrey80
import com.sublime.lingo.presentation.viewmodel.ModelStatus

@Suppress("ktlint:standard:function-naming")
@Composable
fun LanguageSelectionScreen(
    languageType: String?,
    sourceLanguage: String,
    targetLanguage: String,
    modelStatus: ModelStatus,
    onLanguageSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color.White
    val surfaceColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFF3E5F5)
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
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

    val filteredLanguages =
        remember(searchQuery, availableLanguages) {
            if (searchQuery.text.isEmpty()) {
                availableLanguages
            } else {
                availableLanguages.filter { (name, code) ->
                    name.contains(searchQuery.text, ignoreCase = true) ||
                        code.contains(searchQuery.text, ignoreCase = true)
                }
            }
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(backgroundColor),
    ) {
        TopBar(status = modelStatus)
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            isDarkTheme = isDarkTheme,
        )
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(surfaceColor),
        ) {
            items(filteredLanguages) { language ->
                LanguageListItem(
                    languageCode = language.second,
                    onClick = { onLanguageSelect(language.second) },
                    isDarkTheme = isDarkTheme,
                )
            }
        }
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
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF2C2C2C) else Purple80

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        LanguageButton(
            languageItem = sourceLanguage,
            onClick = onSourceLanguageChange,
            isDarkTheme = isDarkTheme,
        )
        AnimatedSwapLanguageButton(
            onSwapLanguages = onSwapLanguages,
            isDarkTheme = isDarkTheme,
        )
        LanguageButton(
            languageItem = targetLanguage,
            onClick = onTargetLanguageChange,
            isDarkTheme = isDarkTheme,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun LanguageButton(
    languageItem: String,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF3D3D3D) else Purple40.copy(alpha = 0.2f)
    val contentColor = if (isDarkTheme) Color.White else Color.Black

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier =
            modifier
                .padding(horizontal = 8.dp)
                .height(40.dp)
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
                color = contentColor,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (isDarkTheme) Color.White else Color.Black

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier =
            modifier
                .fillMaxWidth(),
        placeholder = { Text("Search languages to translate") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.text.isNotEmpty()) {
                IconButton(onClick = { onQueryChange(TextFieldValue("")) }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        colors =
            TextFieldDefaults.textFieldColors(
                containerColor = if (isDarkTheme) Color(0xFF3C3C3C) else PurpleGrey80,
                focusedTextColor = contentColor,
                unfocusedTextColor = contentColor,
                focusedPlaceholderColor = contentColor.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = contentColor.copy(alpha = 0.6f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = contentColor,
            ),
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun LanguageListItem(
    languageCode: String,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    var isSelected by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        if (isSelected) {
            if (isDarkTheme) Color(0xFF3D3D3D) else Color.LightGray
        } else {
            if (isDarkTheme) Color(0xFF1E1E1E) else Color.Transparent
        },
        label = "backgroundColorAnimation",
    )
    val contentColor = if (isDarkTheme) Color.White else Color.Black

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
            color = contentColor,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun AnimatedSwapLanguageButton(
    onSwapLanguages: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    var isRotated by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isRotated) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "rotationAnimation",
    )

    val backgroundColor = if (isDarkTheme) Color(0xFF3D3D3D) else Purple40.copy(alpha = 0.2f)
    val iconResId = if (isDarkTheme) R.drawable.sync_dark_24dp else R.drawable.sync_light_24dp

    IconButton(
        onClick = {
            isRotated = !isRotated
            onSwapLanguages()
        },
        modifier =
            modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(backgroundColor),
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = "Swap languages",
            modifier =
                Modifier
                    .size(24.dp)
                    .rotate(rotationAngle),
        )
    }
}
