package com.sublime.lingo.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sublime.lingo.data.repository.TranslationRepository
import com.sublime.lingo.domain.model.ChatMessage
import com.sublime.lingo.presentation.ui.DeviceIdManager
import com.sublime.lingo.presentation.ui.TranslationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
        private var statusFetchJob: Job? = null

        init {
            loadConversationHistory()
            fetchModelStatus()
        }

        private fun loadConversationHistory() {
            viewModelScope.launch {
                val history = repository.getConversationHistory(deviceId)
                _uiState.update { it.copy(chatMessages = history) }
            }
        }

        private fun fetchModelStatus() {
            statusFetchJob?.cancel()
            statusFetchJob =
                viewModelScope.launch {
                    var retryDelay = 1000L
                    var attempt = 0
                    while (true) {
                        try {
                            val status = repository.getModelStatus()
                            _uiState.update { it.copy(modelStatus = status) }
                            if (status == ModelStatus.READY) break
                        } catch (e: Exception) {
                            _uiState.update { it.copy(modelStatus = ModelStatus.ERROR) }
                        }

                        retryDelay = (retryDelay * 1.5).toLong().coerceAtMost(60000)
                        delay(retryDelay)
                        if (++attempt >= 5) {
                            retryDelay = 1000L
                            attempt = 0
                        }
                    }
                }
        }

        fun updateInputText(newText: String) {
            _uiState.update { it.copy(inputText = newText) }
        }

        private fun setLanguage(
            isSource: Boolean,
            language: String,
        ) {
            val key = if (isSource) "sourceLanguage" else "targetLanguage"
            savedStateHandle[key] = language
        }

        fun swapLanguages() {
            val currentSource = sourceLanguage.value
            setLanguage(true, targetLanguage.value)
            setLanguage(false, currentSource)
        }

        fun sendMessage() {
            val textToTranslate = uiState.value.inputText.trim()
            if (textToTranslate.isBlank()) return

            viewModelScope.launch {
                try {
                    addMessageToChat(ChatMessage(textToTranslate, isUser = true))
                    clearInputAndStartTyping()

                    val translatedText =
                        repository
                            .translate(
                                textToTranslate,
                                sourceLanguage.value,
                                targetLanguage.value,
                            ).getOrThrow()

                    addMessageToChat(ChatMessage(textToTranslate, translatedText, isUser = false))
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        handleTranslationError()
                    }
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
                    translatedText = "Sorry, there was an error processing your request. Please try again.",
                    isUser = false,
                )
            addMessageToChat(errorMessage)
        }

        fun refreshModelStatus() {
            fetchModelStatus()
        }

        fun clearConversationHistory() {
            viewModelScope.launch {
                repository.clearConversationHistory(deviceId)
                _uiState.update { it.copy(chatMessages = emptyList()) }
            }
        }

        override fun onCleared() {
            super.onCleared()
            statusFetchJob?.cancel()
        }
    }

enum class ModelStatus {
    READY,
    LOADING,
    ERROR,
}
