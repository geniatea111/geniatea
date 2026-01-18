package com.example.compose.geniatea.presentation.funcionalidades.chat

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue


sealed class ChatAction {
    object OnBackPressed : ChatAction()
    object OnAccountPressed: ChatAction()
    object OnOptionsPressed: ChatAction()
    data class OnMessageSend(val message: String, val image: Uri?): ChatAction()
    object OnImageSelection: ChatAction()
    data class OnSoundPressed(val message: String): ChatAction()
    data class OnCopyPressed(val message: String): ChatAction()
    object OnStartRecording : ChatAction()
    object OnStopRecording : ChatAction()
    data class OnMessageChange(val value: TextFieldValue) : ChatAction()
}
