package com.sublime.lingo.presentation.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sublime.lingo.data.repository.TranslationRepository
import com.sublime.lingo.presentation.ui.DeviceIdManager
import com.sublime.lingo.presentation.ui.TranslationUiState
import com.sublime.lingo.presentation.ui.main.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
        private val _uiState = MutableStateFlow(TranslationUiState())
        val uiState: StateFlow<TranslationUiState> = _uiState.asStateFlow()

        val sourceLanguage = savedStateHandle.getStateFlow("sourceLanguage", "en")
        val targetLanguage = savedStateHandle.getStateFlow("targetLanguage", "hi")

        private val deviceId: String = deviceIdManager.getDeviceId()

        init {
            loadConversationHistory()
        }

        private fun loadConversationHistory() {
            viewModelScope.launch {
                val history = repository.getConversationHistory(deviceId)
                _uiState.update { it.copy(chatMessages = history) }
            }
        }

        fun updateInputText(newText: String) {
            _uiState.update { it.copy(inputText = newText) }
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
            val textToTranslate = uiState.value.inputText
            if (textToTranslate.isBlank()) return

            viewModelScope.launch {
                addMessageToChat(ChatMessage(textToTranslate, isUser = true))
                clearInputAndStartTyping()

                try {
                    val result =
                        repository.translate(
                            textToTranslate,
                            sourceLanguage.value,
                            targetLanguage.value,
                        )
                    result.fold(
                        onSuccess = { translatedText ->
                            addMessageToChat(
                                ChatMessage(
                                    textToTranslate,
                                    translatedText,
                                    isUser = false,
                                ),
                            )
                        },
                        onFailure = {
                            handleTranslationError()
                        },
                    )
                } catch (e: Exception) {
                    handleTranslationError()
                } finally {
                    stopTyping()
                }
            }
        }

        private suspend fun addMessageToChat(message: ChatMessage) {
            _uiState.update { it.copy(chatMessages = it.chatMessages + message) }
            repository.saveConversationHistoryItem(deviceId, message)
        }

        private fun clearInputAndStartTyping() {
            _uiState.update { it.copy(inputText = "", isTyping = true) }
        }

        private fun stopTyping() {
            _uiState.update { it.copy(isTyping = false) }
        }

        private suspend fun handleTranslationError() {
            val errorMessage =
                ChatMessage(
                    text = "Error!",
                    translatedText = "Sorry, there was an error processing your request. Please try again!",
                    isUser = false,
                )
            addMessageToChat(errorMessage)
        }

        fun clearConversationHistory() {
            viewModelScope.launch {
                repository.clearConversationHistory(deviceId)
                _uiState.update { it.copy(chatMessages = emptyList()) }
            }
        }
    }
