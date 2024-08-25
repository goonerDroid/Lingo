package com.sublime.lingo.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translations")
data class TranslationEntity(
    @PrimaryKey val id: String, // Composite key: text + sourceLang + targetLang
    val text: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String,
    val timestamp: Long,
)
