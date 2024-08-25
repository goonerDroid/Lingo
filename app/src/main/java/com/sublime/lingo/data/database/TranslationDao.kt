package com.sublime.lingo.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translations WHERE text = :text AND sourceLang = :sourceLang AND targetLang = :targetLang LIMIT 1")
    suspend fun getTranslation(
        text: String,
        sourceLang: String,
        targetLang: String,
    ): TranslationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(translation: TranslationEntity)

    @Query("DELETE FROM translations")
    suspend fun deleteAllTranslations()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversationHistoryItem(item: ConversationHistoryEntity)

    @Query("SELECT * FROM conversation_history WHERE deviceId = :deviceId ORDER BY timestamp ASC")
    suspend fun getConversationHistory(deviceId: String): List<ConversationHistoryEntity>

    @Query("DELETE FROM conversation_history WHERE deviceId = :deviceId")
    suspend fun clearConversationHistory(deviceId: String)
}
