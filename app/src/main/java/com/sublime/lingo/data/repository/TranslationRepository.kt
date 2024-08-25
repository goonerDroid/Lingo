package com.sublime.lingo.data.repository

import com.sublime.lingo.data.database.TranslationDao
import com.sublime.lingo.data.database.TranslationEntity
import com.sublime.lingo.data.remote.api.ApiService
import com.sublime.lingo.data.remote.model.TranslationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TranslationRepository
    @Inject
    constructor(
        private val apiService: ApiService,
        private val translationDao: TranslationDao,
    ) {
        suspend fun translate(
            text: String,
            sourceLanguage: String,
            targetLanguage: String,
        ): Result<String> =
            withContext(Dispatchers.IO) {
                try {
                    // Check cache first
                    val cachedTranslation =
                        translationDao.getTranslation(text, sourceLanguage, targetLanguage)
                    if (cachedTranslation != null) {
                        return@withContext Result.success(cachedTranslation.translatedText)
                    }

                    // If not in cache, call API
                    val request = TranslationRequest(text, sourceLanguage, targetLanguage)
                    val response = apiService.translate(request)

                    // Cache the successful translation
                    translationDao.insertTranslation(
                        TranslationEntity(
                            id = "${text}_${sourceLanguage}_$targetLanguage",
                            text = text,
                            translatedText = response.translatedText,
                            sourceLang = sourceLanguage,
                            targetLang = targetLanguage,
                            timestamp = System.currentTimeMillis(),
                        ),
                    )

                    Result.success(response.translatedText)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }
