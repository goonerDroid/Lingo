package com.sublime.lingo.presentation.ui

import com.sublime.lingo.presentation.ui.main.ChatMessage

data class TranslationUiState(
    val inputText: String = "",
    val chatMessages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
)
