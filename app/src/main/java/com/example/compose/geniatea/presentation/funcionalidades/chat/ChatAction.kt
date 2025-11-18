package com.example.compose.geniatea.presentation.funcionalidades.chat

import android.net.Uri

interface ChatAction {
    data object OnBackPressed : ChatAction
    data object OnAccountPressed : ChatAction
    data class OnSoundPressed(val message: String) : ChatAction
    data class OnCopyPressed(val message: String) : ChatAction
    data class OnMessageSend(val message: String, val image: Uri?) : ChatAction
    data object OnOptionsPressed : ChatAction
    data object OnStartRecording : ChatAction
    data object OnStopRecording : ChatAction
    data object OnChatPressed : ChatAction
    data object OnImageSelection : ChatAction
}