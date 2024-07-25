package com.sublime.lingo.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sublime.lingo.data.repository.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranslationViewModel
    @Inject
    constructor(
        private val repository: TranslationRepository,
    ) : ViewModel() {
        fun translate(
            text: String,
            sourceLanguage: String,
            targetLanguage: String,
        ) {
            viewModelScope.launch {
                val result = repository.translate(text, sourceLanguage, targetLanguage)
                result.fold(
                    onSuccess = { translatedText ->
                        // Update UI
                    },
                    onFailure = { error ->
                        // Handle error
                    },
                )
            }
        }
    }
