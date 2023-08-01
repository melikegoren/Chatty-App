package com.melikeg.chatty.domain.model

data class ChatbotRequest(
    val model: String = "text-davinci-003",
    val prompt: String,
    val max_tokens: Int,
    val temperature: Int
    )
