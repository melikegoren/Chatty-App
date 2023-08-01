package com.melikeg.chatty.domain.model

data class ChatbotMessage(
    val sender: String,
    val receiver: String? = null,
    val messageText: String? = null,
)