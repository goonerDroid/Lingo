package com.sublime.lingo.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TranslationResponse(
    @Json(name = "translation") val translatedText: String,
)
