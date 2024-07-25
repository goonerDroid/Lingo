package com.sublime.lingo.data.repository

import com.sublime.lingo.data.remote.api.ApiService
import com.sublime.lingo.data.remote.model.TranslationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TranslationRepository
    @Inject
    constructor(
        private val apiService: ApiService,
    ) {
        suspend fun translate(
            text: String,
            sourceLanguage: String,
            targetLanguage: String,
        ): Result<String> =
            withContext(Dispatchers.IO) {
                try {
                    val request = TranslationRequest(text, sourceLanguage, targetLanguage)
                    val response = apiService.translate(request)
                    Result.success(response.translatedText)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }
