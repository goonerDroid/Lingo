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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    onInputTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onSwapLanguages: () -> Unit,
    onSourceLanguageChange: () -> Unit,
    onTargetLanguageChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
        ChatMessageList(
            messages = chatMessages,
            modifier = Modifier.weight(1f),
        )
        InputArea(
            inputText = inputText,
            onInputTextChange = onInputTextChange,
            onSendClick = onSendClick,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatMessageList(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        reverseLayout = true,
    ) {
        items(messages.reversed()) { message ->
            ChatMessageItem(message)
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
            placeholder = { Text("Type a message") },
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
