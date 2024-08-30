@file:OptIn(
    ExperimentalMaterial3Api::class,
)

package com.sublime.lingo.presentation.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sublime.lingo.domain.model.ChatMessage
import com.sublime.lingo.presentation.ui.formatTimestamp
import com.sublime.lingo.presentation.ui.theme.Pink80
import com.sublime.lingo.presentation.ui.theme.Purple40
import com.sublime.lingo.presentation.ui.theme.Purple80
import com.sublime.lingo.presentation.ui.theme.PurpleGrey80
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatTranslationScreen(
    sourceLanguage: String,
    targetLanguage: String,
    chatMessages: List<ChatMessage>,
    inputText: String,
    isTyping: Boolean,
    onInputTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onSwapLanguages: () -> Unit,
    onSourceLanguageChange: () -> Unit,
    onTargetLanguageChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color.White
    val surfaceColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFF3E5F5)

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(backgroundColor),
    ) {
        TopBar()
        LanguageSelector(
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            onSwapLanguages = onSwapLanguages,
            onSourceLanguageChange = onSourceLanguageChange,
            onTargetLanguageChange = onTargetLanguageChange,
            isDarkTheme = isDarkTheme,
        )
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(surfaceColor),
        ) {
            ChatList(
                chatMessages = chatMessages,
                isTyping = isTyping,
                modifier = Modifier.fillMaxSize(),
                isDarkTheme = isDarkTheme,
            )
        }
        InputArea(
            inputText = inputText,
            onInputTextChange = onInputTextChange,
            onSendClick = onSendClick,
            isDarkTheme = isDarkTheme,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatList(
    chatMessages: List<ChatMessage>,
    isTyping: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToBottomButton = remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            state = listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                AnimatedVisibility(
                    visible = isTyping,
                    enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                    exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
                ) {
                    TypingIndicatorItem(isDarkTheme)
                }
            }
            items(
                items = chatMessages.reversed(),
                key = { it.timestamp },
            ) { message ->
                ChatMessageItem(message = message, isDarkTheme = isDarkTheme)
            }
        }

        AnimatedVisibility(
            visible = showScrollToBottomButton.value,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomEnd),
        ) {
            ScrollToBottomButton(
                onClick = {
                    coroutineScope.launch {
                        val lastIndex = chatMessages.size - 1
                        if (lastIndex > 40) {
                            // For long conversations, scroll quickly
                            listState.scrollToItem(0)
                        } else {
                            // For shorter conversations, use animation
                            listState.animateScrollToItem(0)
                        }
                    }
                },
            )
        }
    }

    LaunchedEffect(chatMessages.size, isTyping) {
        if (chatMessages.isNotEmpty() || isTyping) {
            listState.animateScrollToItem(0)
            showScrollToBottomButton.value = false
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                showScrollToBottomButton.value = index > 2
            }
    }
}

@Suppress("ktlint:compose:modifier-missing-check", "ktlint:standard:function-naming")
@Composable
fun ChatBubble(
    backgroundColor: Color,
    contentColor: Color,
    isUser: Boolean,
    cornerRadius: Dp,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val elevation = if (isDarkTheme) 4.dp else 1.dp
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        tonalElevation = elevation,
        shape =
            RoundedCornerShape(
                topStart = cornerRadius,
                topEnd = cornerRadius,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else cornerRadius,
            ),
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp),
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            content()
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatMessageItem(
    message: ChatMessage,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        if (message.isUser) {
            if (isDarkTheme) Purple80.copy(alpha = 0.5f) else Purple80.copy(alpha = 0.3f)
        } else {
            if (isDarkTheme) Pink80.copy(alpha = 0.5f) else Pink80.copy(alpha = 0.3f)
        }
    val contentColor = if (isDarkTheme) Color.White else Color.Black
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        contentAlignment = alignment,
    ) {
        ChatBubble(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            isUser = message.isUser,
            cornerRadius = 12.dp,
            isDarkTheme = isDarkTheme,
            modifier = Modifier.widthIn(max = if (message.isUser) 280.dp else 320.dp),
        ) {
            Column {
                Text(
                    text = if (message.isUser) message.text else message.translatedText ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun InputArea(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val contentColor = if (isDarkTheme) Color.White else Color.Black

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = inputText,
            onValueChange = onInputTextChange,
            modifier =
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
            placeholder = { Text("Type anything to translate") },
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
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSendClick,
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(if (isDarkTheme) Purple80 else Purple40),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Translate",
                tint = if (isDarkTheme) Color.Black else Color.White,
            )
        }
    }
}
