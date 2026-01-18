package com.example.compose.geniatea.presentation.funcionalidades.chat

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.data.backendConection.ApiService
import com.example.compose.geniatea.data.StoreDataUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class ChatViewModel: ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    private val _actionEvent = MutableLiveData<Event<ChatAction>>()
    val navigationEvent: LiveData<Event<ChatAction>> = _actionEvent

    fun onAction(action: ChatAction) {
        when (action) {
            ChatAction.OnBackPressed -> _actionEvent.value = Event(ChatAction.OnBackPressed)
            ChatAction.OnAccountPressed -> _actionEvent.value = Event(ChatAction.OnAccountPressed)
            ChatAction.OnImageSelection -> _actionEvent.value = Event(ChatAction.OnImageSelection)
            is ChatAction.OnSoundPressed -> _actionEvent.value = Event(ChatAction.OnSoundPressed(action.message))
            is ChatAction.OnCopyPressed -> _actionEvent.value = Event(ChatAction.OnCopyPressed(action.message))
            ChatAction.OnStartRecording -> _actionEvent.value = Event(ChatAction.OnStartRecording)
            ChatAction.OnStopRecording -> _actionEvent.value = Event(ChatAction.OnStopRecording)
            is ChatAction.OnMessageSend -> {
                // Add user message to state
                val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", Locale.getDefault()))
                val userMessage = Message("Usuario", action.message, time, action.image) // TODO: Use string resource for author

                _state.update { it.copy(
                    messages = it.messages + userMessage,
                    currentMessage = TextFieldValue("") // Clear input field
                )}

                // Trigger event to send message to backend
                _actionEvent.value = Event(action)
            }
            ChatAction.OnOptionsPressed -> _actionEvent.value = Event(ChatAction.OnOptionsPressed)
            is ChatAction.OnMessageChange -> {
                _state.update { it.copy(currentMessage = action.value) }
            }
        }
    }

    fun sendMessage(context: Context, messageText: String) {
        viewModelScope.launch {
            try {
                val response = BackendAPI.retrofitService.requestChat(
                    ApiService.MessageRequest(
                        userId = StoreDataUser(context).getId().toString(),
                        message = messageText,
                        image = null // TODO: Handle image sending
                    )
                )

                if (response.isSuccessful) {
                    val messageResponse = response.body()
                    messageResponse?.let {
                        val geniMessage = Message(
                            author = "Geni", // TODO: Use string resource for author
                            content = it.message,
                            timestamp = it.createdAt,
                        )
                        _state.update { currentState ->
                            currentState.copy(
                                messages = currentState.messages + geniMessage
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message: ${e.localizedMessage}")
                Toast.makeText(context, context.getString(R.string.error_sending_message), Toast.LENGTH_LONG).show()
            }
        }
    }
}

open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }
}
