package com.sublime.lingo.presentation.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
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
//        var inputText by mutableStateOf("")
//        private var outputText by mutableStateOf("")
//        var sourceLanguage by mutableStateOf("English")
//        var targetLanguage by mutableStateOf("Spanish")

        fun translate(
            text: String,
            sourceLanguage: String,
            targetLanguage: String,
        ) {
            viewModelScope.launch {
                val result = repository.translate(text, sourceLanguage, targetLanguage)
                result.fold(
                    onSuccess = { translatedText ->
//                        outputText = translatedText
                    },
                    onFailure = { error ->
                        // Handle error
                    },
                )
            }
        }
    }
