package com.sublime.lingo.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TranslationRequest(
    val text: String,
    val sourceLanguage: String,
    val targetLanguage: String,
)
