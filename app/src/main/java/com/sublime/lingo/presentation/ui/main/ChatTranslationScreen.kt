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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sublime.lingo.presentation.ui.formatTimestamp

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
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            TopBar()
            LanguageSelector(
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                onSwapLanguages = onSwapLanguages,
                onSourceLanguageChange = onSourceLanguageChange,
                onTargetLanguageChange = onTargetLanguageChange,
            )
            ChatList(
                chatMessages = chatMessages,
                modifier = Modifier.weight(1f).padding(bottom = 4.dp),
            )
            InputArea(
                inputText = inputText,
                onInputTextChange = onInputTextChange,
                onSendClick = {
                    onSendClick()
                },
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp),
            contentAlignment = Alignment.BottomStart,
        ) {
            TypingIndicatorOverlay(
                isTyping = isTyping,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatList(
    chatMessages: List<ChatMessage>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
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
    AnimatedVisibility(
        visible = isTyping,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
        modifier = modifier,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 4.dp),
        ) {
            TypingIndicator()
        }
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
            modifier = Modifier.weight(0.6f),
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
