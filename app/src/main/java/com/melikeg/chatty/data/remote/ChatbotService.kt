package com.melikeg.chatty.data.remote

import com.melikeg.chatty.domain.model.ChatbotRequest
import com.melikeg.chatty.domain.model.ChatbotResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatbotService {

    @POST("v1/completions")
    fun getChatbotResponse(
        @Header("Authorization") apiKey: String,
        @Body request: ChatbotRequest
    ): Call<ChatbotResponse>
}