package com.melikeg.chatty.domain.model

import android.os.Build
import androidx.annotation.RequiresApi

data class Message(
    var messageId: String?,
    val senderId: String,
    val receiverId: String? = null,
    val messageText: String? = null,
){
    @RequiresApi(Build.VERSION_CODES.O)
    constructor() : this(
        messageId = "",
        senderId = "",
        receiverId = "",
        messageText = "",
    )
}