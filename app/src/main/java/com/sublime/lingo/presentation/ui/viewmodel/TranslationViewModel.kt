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

@HiltViewModel
class TranslationViewModel
    @Inject
    constructor(
        private val repository: TranslationRepository,
    ) : ViewModel() {
        private val _translationResult = MutableStateFlow<String?>(null)
        val translationResult: StateFlow<String?> = _translationResult.asStateFlow()

        fun translate(
            text: String,
            sourceLanguage: String,
            targetLanguage: String,
        ) {
            viewModelScope.launch {
                val result = repository.translate(text, sourceLanguage, targetLanguage)
                result.fold(
                    onSuccess = { translatedText ->
                        _translationResult.value = translatedText
                    },
                    onFailure = { error ->
                        _translationResult.value = "Error: ${error.message}"
                    },
                )
            }
        }
    }
