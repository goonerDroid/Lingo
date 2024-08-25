package com.sublime.lingo.presentation.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sublime.lingo.data.repository.TranslationRepository
import com.sublime.lingo.presentation.ui.DeviceIdManager
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
        deviceIdManager: DeviceIdManager,
    ) : ViewModel() {
        private val _inputText = MutableStateFlow("")
        val inputText: StateFlow<String> = _inputText.asStateFlow()

        private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
        val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

        private val _isTyping = MutableStateFlow(false)
        val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

        val sourceLanguage = savedStateHandle.getStateFlow("sourceLanguage", "en")
        val targetLanguage = savedStateHandle.getStateFlow("targetLanguage", "hi")

        private val deviceId: String = deviceIdManager.getDeviceId()

        init {
            loadConversationHistory()
        }

        private fun loadConversationHistory() {
            viewModelScope.launch {
                val history = repository.getConversationHistory(deviceId)
                _chatMessages.value = history
            }
        }

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
                // Add user message to chat and save it to history
                val userMessage = ChatMessage(textToTranslate, isUser = true)
                _chatMessages.value += userMessage
                repository.saveConversationHistoryItem(deviceId, userMessage)

                // Clear input text
                _inputText.value = ""
                _isTyping.value = true // Start typing indicator

                // Perform translation
                try {
                    val result =
                        repository.translate(
                            textToTranslate,
                            sourceLanguage.value,
                            targetLanguage.value,
                        )
                    result.fold(
                        onSuccess = { translatedText ->
                            val botMessage =
                                ChatMessage(textToTranslate, translatedText, isUser = false)
                            _chatMessages.value += botMessage
                            repository.saveConversationHistoryItem(deviceId, botMessage)
                        },
                        onFailure = { error ->
                            val errorMessage =
                                ChatMessage("Translation failed: ${error.message}", isUser = false)
                            _chatMessages.value += errorMessage
                            repository.saveConversationHistoryItem(deviceId, errorMessage)
                        },
                    )
                } catch (e: Exception) {
                    val errorMessage =
                        ChatMessage("An unexpected error occurred: ${e.message}", isUser = false)
                    _chatMessages.value += errorMessage
                    repository.saveConversationHistoryItem(deviceId, errorMessage)
                } finally {
                    _isTyping.value = false // Stop typing indicator
                }
            }
        }

        fun clearConversationHistory() {
            viewModelScope.launch {
                repository.clearConversationHistory(deviceId)
                _chatMessages.value = emptyList()
            }
        }
    }
