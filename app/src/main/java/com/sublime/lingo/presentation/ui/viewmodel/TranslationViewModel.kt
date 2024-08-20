package com.sublime.lingo.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sublime.lingo.data.repository.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("MemberVisibilityCanBePrivate")
@HiltViewModel
class TranslationViewModel
    @Inject
    constructor(
        private val repository: TranslationRepository,
    ) : ViewModel() {
        private val _inputText = MutableStateFlow("")
        val inputText: StateFlow<String> = _inputText.asStateFlow()

        private val _translationResult = MutableStateFlow<TranslationState>(TranslationState.Idle)
        val translationResult: StateFlow<TranslationState> = _translationResult.asStateFlow()

        private val _sourceLanguage = MutableStateFlow("en")
        val sourceLanguage: StateFlow<String> = _sourceLanguage.asStateFlow()

        private val _targetLanguage = MutableStateFlow("hi")
        val targetLanguage: StateFlow<String> = _targetLanguage.asStateFlow()

        fun updateInputText(newText: String) {
            _inputText.value = newText
        }

        fun setSourceLanguage(language: String) {
            _sourceLanguage.value = language
        }

        fun setTargetLanguage(language: String) {
            _targetLanguage.value = language
        }

        fun translate() {
            viewModelScope.launch {
                val textToTranslate = inputText.value
                if (textToTranslate.isBlank()) {
                    _translationResult.value = TranslationState.Error("Please enter text to translate")
                    return@launch
                }

                _translationResult.value = TranslationState.Loading
                try {
                    val result =
                        repository.translate(
                            textToTranslate,
                            sourceLanguage.value,
                            targetLanguage.value,
                        )
                    result.fold(
                        onSuccess = { translatedText ->
                            _translationResult.value = TranslationState.Success(translatedText)
                        },
                        onFailure = { error ->
                            _translationResult.value =
                                TranslationState.Error("Translation failed: ${error.message}")
                        },
                    )
                } catch (e: Exception) {
                    _translationResult.value =
                        TranslationState.Error("An unexpected error occurred: ${e.message}")
                }
            }
        }
    }

sealed class TranslationState {
    data object Idle : TranslationState()

    data object Loading : TranslationState()

    data class Success(
        val translatedText: String,
    ) : TranslationState()

    data class Error(
        val message: String,
    ) : TranslationState()
}
