package com.sublime.lingo.data.remote.api

import com.sublime.lingo.data.remote.model.ModelStatusResponse
import com.sublime.lingo.data.remote.model.TranslationRequest
import com.sublime.lingo.data.remote.model.TranslationResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("translate")
    suspend fun translate(
        @Body request: TranslationRequest,
    ): TranslationResponse

    @GET("state")
    suspend fun getModelStatus(): ModelStatusResponse
}
