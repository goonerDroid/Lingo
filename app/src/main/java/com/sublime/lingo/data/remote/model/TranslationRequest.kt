package com.sublime.lingo.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TranslationRequest(
    @Json(name = "text") val text: String,
    @Json(name = "source_lang") val sourceLang: String,
    @Json(name = "target_lang") val targetLang: String,
)
