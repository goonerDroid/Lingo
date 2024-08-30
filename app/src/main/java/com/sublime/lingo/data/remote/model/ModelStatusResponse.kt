package com.sublime.lingo.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelStatusResponse(
    @Json(name = "status") val status: String,
    @Json(name = "message") val message: String,
)
