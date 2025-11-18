package com.example.compose.geniatea.presentation.funcionalidades.chat

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.data.backendConection.ApiService
import com.example.compose.geniatea.data.StoreDataUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


class ChatViewModel: ViewModel() {

    private val _state = MutableStateFlow(ChatState(
        initialMessages = listOf(

        )
    ))

    private val _state2 = MutableStateFlow(ChatState(
        initialMessages = listOf(
          Message(
                author = "Usuario",
                content = "Hola",
                timestamp = "10:01 12/10/2025"
            ),
            Message(
                author = "Geni",
                content = "De nada, estoy aquí para ayudarte. Si tienes más preguntas, no dudes en preguntar.",
                timestamp = "10:06 10/10/2025",
            ),
            Message(
                author = "Usuario",
                content = "Gracias, Geni. Eso es muy útil.",
                timestamp = "10:05 10/10/2025"
            ),
            Message(
                author = "Geni",
                content = "Para cambiar tu contraseña, ve a la sección de configuración de tu cuenta y selecciona 'Cambiar contraseña'.",
                timestamp = "10:04 10/10/2025",
            ),
            Message(
                author = "Usuario",
                content = "¿Cómo puedo cambiar mi contraseña?",
                timestamp = "10:03 10/10/2025"
            ),
            Message(
                author = "Geni",
                content = "Claro, ¿qué pregunta tienes?",
                timestamp = "10:02 10/10/2025",
            ),
            Message(
                author = "Usuario",
                content = "Hola, tengo una pregunta sobre mi cuenta.",
                timestamp = "10:01 10/10/2025"
            ),
        )
    ))
    var state = _state
    private val _actionEvent = MutableLiveData<Event<ChatAction>>()
    val navigationEvent: LiveData<Event<ChatAction>> = _actionEvent

    fun onAction(action: ChatAction) {
        when (action) {
            ChatAction.OnBackPressed -> {
                _actionEvent.value = Event(ChatAction.OnBackPressed)
            }
            ChatAction.OnAccountPressed -> {
                _actionEvent.value = Event(ChatAction.OnAccountPressed)
            }
            ChatAction.OnImageSelection -> {
                _actionEvent.value = Event(ChatAction.OnImageSelection)
            }
            is ChatAction.OnSoundPressed -> {
                _actionEvent.value = Event(ChatAction.OnSoundPressed(action.message))
            }
            is ChatAction.OnCopyPressed -> {
                _actionEvent.value = Event(ChatAction.OnCopyPressed(action.message))
            }
            ChatAction.OnStartRecording -> {
                _actionEvent.value = Event(ChatAction.OnStartRecording)
            }
            ChatAction.OnStopRecording -> {
                _actionEvent.value = Event(ChatAction.OnStopRecording)
            }
            is ChatAction.OnMessageSend -> {
                _actionEvent.value = Event(ChatAction.OnMessageSend(action.message, action.image))
            }
            ChatAction.OnOptionsPressed -> {
                _actionEvent.value = Event(ChatAction.OnOptionsPressed)
            }

        }
    }

    suspend fun sendMessage(context: Context) {
        try{
            val response = BackendAPI.retrofitService.requestChat(
                ApiService.MessageRequest(
                    userId = StoreDataUser().getId(context).toString(),
                    message = state.value.currentMessage.text,
                    image = null
                )
            )

            if (response.isSuccessful) {
                val messageResponse = response.body()
                messageResponse?.let {
                    _state.update { currentState ->
                        currentState.apply {
                            addMessage(
                                Message(
                                    author = "Geni",
                                    content = messageResponse.message,
                                    timestamp = messageResponse.createdAt,
                                )
                            )
                            currentMessage = currentMessage.copy(text = "")
                            selectedImage.value = null
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Error sending message: ${e.localizedMessage}")
            Toast.makeText(context, context.getString(R.string.error_sending_message), Toast.LENGTH_LONG).show()
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