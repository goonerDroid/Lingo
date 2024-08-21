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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Function to scroll to bottom
    fun scrollToBottom() {
        coroutineScope.launch {
            listState.animateScrollToItem(0)
        }
    }

    // Calculate if we should show the scroll button
    val showScrollButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 20 // Show after scrolling past 20 items
        }
    }

    // Automatically scroll to bottom when a new message is added or typing starts
    LaunchedEffect(chatMessages.size, isTyping) {
        if (listState.firstVisibleItemIndex == 0) {
            scrollToBottom()
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        TopBar()
        LanguageSelector(
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            onSwapLanguages = onSwapLanguages,
            onSourceLanguageChange = onSourceLanguageChange,
            onTargetLanguageChange = onTargetLanguageChange,
        )
        Box(modifier = Modifier.weight(1f)) {
            ChatList(
                chatMessages = chatMessages,
                listState = listState,
                modifier = Modifier.fillMaxSize(),
            )
            AnimatedScrollToBottomButton(
                visible = showScrollButton,
                onClick = { scrollToBottom() },
            )
            TypingIndicatorOverlay(
                isTyping = isTyping,
                modifier = Modifier.align(Alignment.BottomStart),
            )
        }
        InputArea(
            inputText = inputText,
            onInputTextChange = onInputTextChange,
            onSendClick = {
                onSendClick()
                scrollToBottom()
            },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatList(
    chatMessages: List<ChatMessage>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = modifier,
    ) {
        items(
            items = chatMessages.reversed(),
            key = { it.timestamp },
        ) { message ->
            ChatMessageItem(message = message)
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun TypingIndicatorOverlay(
    isTyping: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomStart,
    ) {
        AnimatedVisibility(
            visible = isTyping,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            TypingIndicator(
                modifier =
                    Modifier
                        .padding(start = 16.dp, bottom = 8.dp, top = 4.dp),
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun AnimatedScrollToBottomButton(
    visible: Boolean,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
    ) {
        ScrollToBottomButton(onClick = onClick)
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ScrollToBottomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd,
    ) {
        IconButton(
            onClick = onClick,
            modifier =
                Modifier
                    .padding(16.dp)
                    .size(48.dp)
                    .background(Color.Gray, CircleShape),
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Scroll to bottom",
                tint = Color.White,
            )
        }
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

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(8.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
    ) {
        if (!message.isUser) {
            MessageIcon(isUser = false)
            Spacer(modifier = Modifier.width(4.dp))
        }

        Column {
            Box(
                modifier =
                    Modifier
                        .widthIn(max = 250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .padding(12.dp),
            ) {
                Column {
                    if (message.isUser) {
                        Text(
                            text = message.text,
                            color = Color.Black,
                        )
                    } else {
                        Text(
                            text = message.translatedText ?: "",
                            color = Color.Black,
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

        if (message.isUser) {
            Spacer(modifier = Modifier.width(4.dp))
            MessageIcon(isUser = true)
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
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray.copy(alpha = 0.1f))
                .padding(8.dp),
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
        label = "",
    )

    Box(
        modifier =
            modifier
                .size(8.dp * scale)
                .background(Color.Gray, CircleShape),
    )
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
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
                .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = inputText,
            onValueChange = onInputTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a sentence to translate") },
            singleLine = true,
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onSendClick) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Translate")
        }
    }
}

data class ChatMessage(
    val text: String,
    val translatedText: String? = null,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)
