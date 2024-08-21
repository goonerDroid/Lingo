package com.sublime.lingo.presentation.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sublime.lingo.data.repository.TranslationRepository
import com.sublime.lingo.presentation.ui.main.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranslationViewModel
    @Inject
    constructor(
        private val repository: TranslationRepository,
        private val savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _inputText = MutableStateFlow("")
        val inputText: StateFlow<String> = _inputText.asStateFlow()

        private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
        val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

        val sourceLanguage = savedStateHandle.getStateFlow("sourceLanguage", "en")
        val targetLanguage = savedStateHandle.getStateFlow("targetLanguage", "hi")

        fun updateInputText(newText: String) {
            _inputText.value = newText
        }

        fun setSourceLanguage(language: String) {
            savedStateHandle["sourceLanguage"] = language
        }

        fun setTargetLanguage(language: String) {
            savedStateHandle["targetLanguage"] = language
        }

        fun swapLanguages() {
            val currentSource = sourceLanguage.value
            val currentTarget = targetLanguage.value

            setSourceLanguage(currentTarget)
            setTargetLanguage(currentSource)
        }

        fun sendMessage() {
            val textToTranslate = _inputText.value
            if (textToTranslate.isBlank()) return

            viewModelScope.launch {
                _chatMessages.value += ChatMessage(textToTranslate, isUser = true)
                _inputText.value = ""

                try {
                    val result =
                        repository.translate(
                            textToTranslate,
                            sourceLanguage.value,
                            targetLanguage.value,
                        )
                    result.fold(
                        onSuccess = { translatedText ->
                            _chatMessages.value +=
                                ChatMessage(
                                    textToTranslate,
                                    translatedText,
                                    isUser = false,
                                )
                        },
                        onFailure = { error ->
                            _chatMessages.value +=
                                ChatMessage(
                                    "Translation failed: ${error.message}",
                                    isUser = false,
                                )
                        },
                    )
                } catch (e: Exception) {
                    _chatMessages.value +=
                        ChatMessage(
                            "An unexpected error occurred: ${e.message}",
                            isUser = false,
                        )
                }
            }
        }
    }
