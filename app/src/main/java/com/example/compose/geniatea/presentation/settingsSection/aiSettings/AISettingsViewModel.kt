package com.example.compose.geniatea.presentation.settingsSection.aiSettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.data.backendConection.ApiService
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class AISettingsViewModel: ViewModel() {

    private val _state = MutableStateFlow(AISettingsState())
    val state = _state.asStateFlow()

    private val _actionEvent = MutableLiveData<Event<AISettingsAction>>()
    val actionEvent: LiveData<Event<AISettingsAction>> = _actionEvent

    private var userToken: String? = null
    private var userId: Long? = null

    fun loadSettings(context: Context) {
        viewModelScope.launch {
            try {
                val store = StoreDataUser(context)
                userToken = store.getToken()
                userId = store.getId()
                val token = userToken ?: ""
                val response = BackendAPI.retrofitService.getUserPreferences("Bearer $token")
                if (response.isSuccessful) {
                    val prefs = response.body()
                    prefs?.let {DTO ->
                         _state.update { currentState ->
                            currentState.copy(
                                isClearLanguage = DTO.clearLanguage ?: false,
                                avatarSource = if (DTO.showAvatar == true) AvatarSource.GALLERY else AvatarSource.GENI,
                                fontSize = when (DTO.fontSize) {
                                    "S" -> 0
                                    "M" -> 1
                                    "L" -> 2
                                    else -> 1
                                },
                                responseStyle = when (DTO.responseStyle) {
                                    "concise" -> 0f
                                    "normal" -> 1f
                                    "learning" -> 2f
                                    else -> 1f
                                },
                                showPictograms = DTO.showPictograms ?: false
                            )
                        }

                        if (DTO.showAvatar == true) {
                            fetchAvatar()
                        }
                    }
                } else {
                    Log.e("AISettingsViewModel", "Error fetching preferences: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AISettingsViewModel", "Exception fetching preferences", e)
            }
        }
    }

    fun onAction(action: AISettingsAction) {
        when (action) {
            AISettingsAction.OnBackPressed -> {
                _actionEvent.value = Event(AISettingsAction.OnBackPressed)
            }
            is AISettingsAction.OnClearLanguageToggle -> {
                _state.update { it.copy(isClearLanguage = action.isChecked) }
                saveSettings("Lenguaje claro actualizado")
            }
            is AISettingsAction.OnShowPictogramsToggle -> {
                _state.update { it.copy(showPictograms = action.isChecked) }
                saveSettings("Pictogramas actualizados")
            }
            is AISettingsAction.OnResponseStyleChange -> {
                _state.update { it.copy(responseStyle = action.value) }
                // Slider invokes this continuously, might need debounce. But for now implementing as requested.
                // NOTE: User probably wants on drag end, but action is vague.
                // Assuming this is called on change.
                saveSettings("Estilo de respuesta actualizado")
            }
            is AISettingsAction.OnFontSizeChange -> {
                _state.update { it.copy(fontSize = action.size) }
                saveSettings("Tamaño de fuente actualizado")
            }
            is AISettingsAction.OnAvatarSourceChange -> {
                if (action.source == AvatarSource.GALLERY) {
                    _actionEvent.value = Event(AISettingsAction.OpenGallery)
                    // State update deferred until image selection or user cancels/switches back
                    // Actually, consistent with other settings, we might want to optimistic update
                    // But for Gallery, we need the file.
                    // Let's optimistic update the UI selector, but not save until file picked?
                    // Or only update UI if file picked?
                    // User request: "si se selecciona foto de la galeria, se debe abrir... el usuario puede seleccionar... que sera enviada"
                    _state.update { it.copy(avatarSource = action.source) }
                } else {
                    _state.update { it.copy(avatarSource = action.source) }
                    saveSettings("Fuente de avatar actualizada") // Updates preferences
                    updateAvatar(null, "Avatar removido") // Updates avatar specifically to null
                }
            }
            is AISettingsAction.ShowToast -> {
                 _actionEvent.value = Event(action)
            }
            AISettingsAction.OpenGallery -> { /* No-op here, handled by view */ }
        }
    }

    fun onAvatarSelected(uri: android.net.Uri, context: Context) {
        // Create MultipartBody.Part from Uri
        val contentResolver = context.contentResolver
        val type = contentResolver.getType(uri) ?: "image/*"
        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()

        if (bytes != null) {
            val requestFile = okhttp3.RequestBody.create(
               type.toMediaTypeOrNull(),
                bytes
            )
            val body = okhttp3.MultipartBody.Part.createFormData("avatar", "avatar.jpg", requestFile)
            updateAvatar(body, "Avatar actualizado")

            // Update local state immediately
            val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            _state.update { it.copy(avatarBitmap = bitmap, avatarSource = AvatarSource.GALLERY) }
        } else {
            Log.e("AISettingsViewModel", "Could not read file from Uri")
        }
    }

    private fun updateAvatar(avatar: okhttp3.MultipartBody.Part?, message: String) {
        val token = userToken ?: return
        val uId = userId ?: return

        viewModelScope.launch {
             try {
                 val response = BackendAPI.retrofitService.updateAvatar("Bearer $token", uId, avatar)
                 if (response.isSuccessful) {
                     _actionEvent.value = Event(AISettingsAction.ShowToast(message))
                 } else {
                     Log.e("AISettingsViewModel", "Error updating avatar: ${response.code()}")
                 }
             } catch (e: Exception) {
                 Log.e("AISettingsViewModel", "Exception updating avatar", e)
             }
        }
    }

    private fun fetchAvatar() {
        val token = userToken ?: return
        val uId = userId ?: return

        viewModelScope.launch {
            try {
                val response = BackendAPI.retrofitService.getAvatar("Bearer $token", uId)
                if (response.isSuccessful) {
                    val bytes = response.body()?.bytes()
                    if (bytes != null) {
                        val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                         _state.update { it.copy(avatarBitmap = bitmap) }
                    }
                } else {
                     Log.e("AISettingsViewModel", "Error fetching avatar: ${response.code()}")
                }
            } catch (e: Exception) {
                 Log.e("AISettingsViewModel", "Exception fetching avatar", e)
            }
        }
    }


    private fun saveSettings(changeMessage: String) {
        val token = userToken ?: return
        val currentState = _state.value
        
        viewModelScope.launch {
            try {
                val dto = ApiService.UserPreferenceDTO(
                    showPictograms = currentState.showPictograms,
                    language = null, // Ignored
                    showAvatar = currentState.avatarSource == AvatarSource.GALLERY,
                    clearLanguage = currentState.isClearLanguage,
                    responseStyle = when (currentState.responseStyle) {
                        0f -> "concise"
                        1f -> "normal"
                        2f -> "learning"
                        else -> "normal"
                    },
                    fontSize = when (currentState.fontSize) {
                        0 -> "S"
                        1 -> "M"
                        2 -> "L"
                        else -> "M"
                    }
                )

                val response = BackendAPI.retrofitService.updateUserPreferences("Bearer $token", dto)
                if (response.isSuccessful) {
                    _actionEvent.value = Event(AISettingsAction.ShowToast(changeMessage))
                } else {
                    Log.e("AISettingsViewModel", "Error updating preferences: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AISettingsViewModel", "Exception updating preferences", e)
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