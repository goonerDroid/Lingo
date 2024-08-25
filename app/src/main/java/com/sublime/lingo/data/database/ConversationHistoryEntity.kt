package com.sublime.lingo.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversation_history")
data class ConversationHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val message: String,
    val translatedMessage: String?,
    val isUser: Boolean,
    val timestamp: Long,
)
