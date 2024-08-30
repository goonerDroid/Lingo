package com.sublime.lingo.presentation.ui

import com.sublime.lingo.domain.model.ChatMessage
import com.sublime.lingo.presentation.viewmodel.ModelStatus

data class TranslationUiState(
    val inputText: String = "",
    val chatMessages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val modelStatus: ModelStatus = ModelStatus.LOADING, // Default to LOADING when the app starts
)
