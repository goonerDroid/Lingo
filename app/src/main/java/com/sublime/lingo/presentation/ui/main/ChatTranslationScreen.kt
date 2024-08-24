@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.sublime.lingo.presentation.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sublime.lingo.presentation.ui.formatTimestamp
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
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        TopBar()
        LanguageSelector(
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            onSwapLanguages = onSwapLanguages,
            onSourceLanguageChange = onSourceLanguageChange,
            onTargetLanguageChange = onTargetLanguageChange,
        )
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) {
            ChatList(
                chatMessages = chatMessages,
                isTyping = isTyping,
                modifier = Modifier.fillMaxSize(),
            )
        }
        InputArea(
            inputText = inputText,
            onInputTextChange = onInputTextChange,
            onSendClick = onSendClick,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatList(
    chatMessages: List<ChatMessage>,
    isTyping: Boolean,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToBottomButton =
        remember { derivedStateOf { listState.firstVisibleItemIndex > 1 } }

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
                    TypingIndicatorItem()
                }
            }
            items(
                items = chatMessages.reversed(),
                key = { it.timestamp },
            ) { message ->
                ChatMessageItem(message = message)
            }
        }

        if (showScrollToBottomButton.value) {
            ScrollToBottomButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
    }

    LaunchedEffect(chatMessages.size, isTyping) {
        if (chatMessages.isNotEmpty() || isTyping) {
            listState.animateScrollToItem(0)
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun TypingIndicatorItem(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 4.dp),
    ) {
        ChatBubble(
            backgroundColor = Color.Gray.copy(alpha = 0.1f),
            contentColor = Color.Black,
            cornerRadius = 16.dp,
        ) {
            TypingIndicator()
        }
    }
}

@Suppress("ktlint:compose:modifier-missing-check", "ktlint:standard:function-naming")
@Composable
fun ChatBubble(
    backgroundColor: Color,
    contentColor: Color,
    cornerRadius: Dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = cornerRadius,
                        topEnd = cornerRadius,
                        bottomStart = 4.dp,
                        bottomEnd = cornerRadius,
                    ),
                ).background(backgroundColor)
                .padding(12.dp),
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ScrollToBottomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        modifier =
            modifier
                .padding(18.dp)
                .size(36.dp)
                .clip(RoundedCornerShape(28.dp)),
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = "Scroll to bottom",
            tint = Color.White,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatMessageItem(
    message: ChatMessage,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        if (message.isUser) Color.Blue.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f)
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = alignment,
    ) {
        Column(
            modifier =
                Modifier
                    .widthIn(max = 280.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (message.isUser) 16.dp else 4.dp,
                                bottomEnd = if (message.isUser) 4.dp else 16.dp,
                            ),
                        ).background(backgroundColor)
                        .padding(12.dp),
            ) {
                Column {
                    if (message.isUser) {
                        Text(
                            text = message.text,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    } else {
                        Text(
                            text = message.translatedText ?: "",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatTimestamp(message.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.End),
                    )
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun MessageIcon(
    isUser: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(40.dp)
                .background(Color.LightGray.copy(alpha = 0.3f), CircleShape)
                .padding(8.dp),
    ) {
        Icon(
            imageVector = if (isUser) Icons.Default.Person else Icons.Default.Info,
            contentDescription = if (isUser) "User" else "Translation",
            tint = Color.Gray,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .widthIn(max = 70.dp)
                .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            AnimatedDot()
            if (index < 2) Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun AnimatedDot(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "Animated Dots",
    )

    Box(
        modifier =
            modifier
                .size(8.dp)
                .scale(scale)
                .background(Color.Gray, CircleShape),
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun InputArea(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = inputText,
            onValueChange = onInputTextChange,
            modifier =
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
            placeholder = { Text("Type a sentence to translate") },
            singleLine = true,
            colors =
                TextFieldDefaults.textFieldColors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSendClick,
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.primary),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Translate",
                tint = Color.White,
            )
        }
    }
}

data class ChatMessage(
    val text: String,
    val translatedText: String? = null,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)
