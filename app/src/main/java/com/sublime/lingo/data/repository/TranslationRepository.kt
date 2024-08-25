package com.sublime.lingo.data.repository

import com.sublime.lingo.data.database.ConversationHistoryEntity
import com.sublime.lingo.data.database.TranslationDao
import com.sublime.lingo.data.database.TranslationEntity
import com.sublime.lingo.data.remote.api.ApiService
import com.sublime.lingo.data.remote.model.TranslationRequest
import com.sublime.lingo.presentation.ui.main.ChatMessage
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

        suspend fun saveConversationHistoryItem(
            deviceId: String,
            chatMessage: ChatMessage,
        ) {
            withContext(Dispatchers.IO) {
                val historyItem =
                    ConversationHistoryEntity(
                        deviceId = deviceId,
                        message = chatMessage.text,
                        translatedMessage = chatMessage.translatedText,
                        isUser = chatMessage.isUser,
                        timestamp = chatMessage.timestamp,
                    )
                translationDao.insertConversationHistoryItem(historyItem)
            }
        }

        suspend fun getConversationHistory(deviceId: String): List<ChatMessage> =
            withContext(Dispatchers.IO) {
                translationDao.getConversationHistory(deviceId).map { entity ->
                    ChatMessage(
                        text = entity.message,
                        translatedText = entity.translatedMessage,
                        isUser = entity.isUser,
                        timestamp = entity.timestamp,
                    )
                }
            }

        suspend fun clearConversationHistory(deviceId: String) {
            withContext(Dispatchers.IO) {
                translationDao.clearConversationHistory(deviceId)
            }
        }
    }
