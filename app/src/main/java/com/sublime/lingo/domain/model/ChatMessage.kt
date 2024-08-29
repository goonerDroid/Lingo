package com.sublime.lingo.domain.model

data class ChatMessage(
    val text: String,
    val translatedText: String? = null,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)
