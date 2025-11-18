package com.example.compose.geniatea.presentation.funcionalidades.chat

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.input.TextFieldValue


class ChatState(initialMessages: List<Message>) {
    var currentMessage by mutableStateOf(TextFieldValue(""))

    private val _messages: MutableList<Message> = initialMessages.toMutableStateList()
    val messages: List<Message> = _messages

    fun addMessage(msg: Message) {
        _messages.add(msg) // Add to the beginning of the list
    }

    val selectedImage = mutableStateOf<Uri?>(null)
}


@Immutable
data class Message(
    val author: String,
    val content: String,
    val timestamp: String,
    val image: Uri? = null,
   // val authorImage: Int = R.drawable.geni
)
