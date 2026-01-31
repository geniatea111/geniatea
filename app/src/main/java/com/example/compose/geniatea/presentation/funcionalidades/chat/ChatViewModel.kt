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
import kotlinx.coroutines.Dispatchers
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class ChatViewModel: ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    private val _actionEvent = MutableLiveData<Event<ChatAction>>()
    val navigationEvent: LiveData<Event<ChatAction>> = _actionEvent

    private var sessionId: Long = -1L

    fun setSessionId(id: Long) {
        sessionId = id
    }

    fun setTopic(topic: String) {
        _state.update { it.copy(topic = topic) }
    }

    fun onAction(action: ChatAction) {
        when (action) {
            ChatAction.OnBackPressed -> _actionEvent.value = Event(ChatAction.OnBackPressed)
            ChatAction.OnAccountPressed -> _actionEvent.value = Event(ChatAction.OnAccountPressed)
            ChatAction.OnImageSelection -> {
                Log.d("ChatViewModel", "Action: OnImageSelection")
                _actionEvent.value = Event(ChatAction.OnImageSelection)
            }
            is ChatAction.OnImagePicked -> {
                Log.d("ChatViewModel", "Action: OnImagePicked. URI: ${action.uri}")
                _state.update { it.copy(selectedImage = action.uri) }
            }
            is ChatAction.OnSoundPressed -> _actionEvent.value = Event(ChatAction.OnSoundPressed(action.message))
            is ChatAction.OnCopyPressed -> _actionEvent.value = Event(ChatAction.OnCopyPressed(action.message))
            ChatAction.OnStartRecording -> _actionEvent.value = Event(ChatAction.OnStartRecording)
            ChatAction.OnStopRecording -> _actionEvent.value = Event(ChatAction.OnStopRecording)
            is ChatAction.OnMessageSend -> {
                // Add user message to state
                val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", Locale.getDefault()))
                val userMessage = Message(author = "Usuario", content = action.message, timestamp = time, image = action.image)

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
            is ChatAction.OnStyleChange -> {
                _state.update { it.copy(chatStyle = action.style) }
            }
        }
    }

    fun sendMessage(context: Context, messageText: String) {
        viewModelScope.launch {
            _state.update { it.copy(isGenerating = true) }
            val currentImageUri = _state.value.selectedImage
            Log.d("ChatViewModel", "Sending message. Selected Image URI: $currentImageUri")

            withContext(Dispatchers.IO) {
                try {
                    var base64Image: String? = null
                    if (currentImageUri != null) {
                       try {
                           val inputStream = context.contentResolver.openInputStream(currentImageUri)
                           val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                           inputStream?.close()
                           
                           if (bitmap != null) {
                               val outputStream = ByteArrayOutputStream()
                               bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, outputStream)
                               val jpegBytes = outputStream.toByteArray()
                               base64Image = Base64.encodeToString(jpegBytes, Base64.NO_WRAP)
                               Log.d("ChatViewModel", "Image converted to JPEG Base64. Length: ${base64Image.length}")
                           } else {
                               Log.e("ChatViewModel", "Failed to decode bitmap from URI")
                           }
                       } catch (e: Exception) {
                           Log.e("ChatViewModel", "Error encoding image: ${e.localizedMessage}")
                       }
                    } else {
                        Log.d("ChatViewModel", "No image selected")
                    }

                    // Clear image before causing the network request, to update UI immediately
                    _state.update { it.copy(selectedImage = null) }

                    val response = BackendAPI.retrofitService.requestChat(
                        ApiService.MessageRequest(
                            sessionId = if (sessionId != -1L) sessionId else null,
                            message = messageText,
                            image = base64Image
                        )
                    )

                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            val source = body.source()
                            var accumulatedText = ""
                            var isFirstMessage = true
                            
                            while (!source.exhausted()) {
                                val line = source.readUtf8Line()
                                if (line != null && line.startsWith("data:")) {
                                    val content = line.substring(5)
                                    // Check for session ID (trimming leading space if present for the check)
                                    if (content.trimStart().startsWith("SESSION_ID:")) {
                                        val newSessionId = content.substringAfter("SESSION_ID:").trim().toLongOrNull()
                                        if (newSessionId != null) {
                                            sessionId = newSessionId
                                            Log.d("ChatViewModel", "Session ID updated: $sessionId")
                                        }
                                    } else {
                                        // Normal content
                                        accumulatedText += content

                                        if (isFirstMessage) {
                                            val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", Locale.getDefault()))
                                            val geniMessage = Message(
                                                author = "Geni", // TODO: Use string resource for author
                                                content = accumulatedText,
                                                timestamp = time,
                                            )
                                            _state.update { currentState ->
                                                currentState.copy(
                                                    messages = currentState.messages + geniMessage
                                                )
                                            }
                                            isFirstMessage = false
                                        } else {
                                            _state.update { currentState ->
                                                if (currentState.messages.isNotEmpty()) {
                                                    val messages = currentState.messages.toMutableList()
                                                    val lastIndex = messages.lastIndex
                                                    val lastMsg = messages[lastIndex]
                                                    messages[lastIndex] = lastMsg.copy(content = accumulatedText)
                                                    currentState.copy(messages = messages)
                                                } else {
                                                    currentState
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    } else {
                        Log.e("ChatViewModel", "Error response: ${response.code()} ${response.message()}")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) { 
                    Log.e("ChatViewModel", "Error sending message: ${e.localizedMessage}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, context.getString(R.string.error_sending_message), Toast.LENGTH_LONG).show()
                    }
                } finally {
                    _state.update { it.copy(isGenerating = false) }
                }
            }
        }
    }

    fun loadChatSession(context: Context) {
        if (sessionId == -1L) return

        viewModelScope.launch {
            try {
                val token = StoreDataUser(context).getToken()
                if (token != null) {
                    val response = BackendAPI.retrofitService.getChatSession("Bearer $token", sessionId)
                    if (response.isSuccessful) {
                        val messages = response.body()?.map { chatHistoryResponse ->
                            Message(
                                author = chatHistoryResponse.sender,
                                content = chatHistoryResponse.message,
                                timestamp = chatHistoryResponse.createdAt
                            )
                        } ?: emptyList()
                        _state.update { it.copy(messages = messages) }
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error loading chat session: ${e.localizedMessage}")
                Toast.makeText(context, "Error loading chat session", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun loadAvatar(context: Context) {
        viewModelScope.launch {
            val avatar = com.example.compose.geniatea.data.repository.AvatarRepository(context).getAvatar()
            if (avatar != null) {
                _state.update { it.copy(userAvatar = avatar) }
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
