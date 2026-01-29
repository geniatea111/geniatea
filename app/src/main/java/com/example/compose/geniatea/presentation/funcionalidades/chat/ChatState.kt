package com.example.compose.geniatea.presentation.funcionalidades.chat

import android.net.Uri
import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue

data class ChatState(
    val messages: List<Message> = emptyList(),
    val currentMessage: TextFieldValue = TextFieldValue(""),
    val selectedImage: Uri? = null,
    val chatStyle: ChatStyle = ChatStyle.NORMAL,
    val topic: String = "",
    val userAvatar: Bitmap? = null
)

@Immutable
data class Message(
    val author: String,
    val content: String,
    val timestamp: String,
    val image: Uri? = null,
   // val authorImage: Int = R.drawable.geni
)
