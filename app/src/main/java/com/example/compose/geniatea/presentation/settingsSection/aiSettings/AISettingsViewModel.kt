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

class AISettingsViewModel(application: android.app.Application): androidx.lifecycle.AndroidViewModel(application) {

    private val _state = MutableStateFlow(AISettingsState())
    val state = _state.asStateFlow()

    private val _actionEvent = MutableLiveData<Event<AISettingsAction>>()
    val actionEvent: LiveData<Event<AISettingsAction>> = _actionEvent

    private var userToken: String? = null
    private var userId: Long? = null

    // Helper to get store
    private val storeDataUser: StoreDataUser
        get() = StoreDataUser(getApplication<android.app.Application>().applicationContext)

    fun loadSettings(context: Context) { // Keep signature for compatibility if needed, or remove param
        viewModelScope.launch {
            try {
                // Use the passed context or the application context
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
                                showPictograms = DTO.showPictograms ?: false,
                                language = DTO.language
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
                saveSettings("Estilo de respuesta actualizado")
            }
            is AISettingsAction.OnFontSizeChange -> {
                _state.update { it.copy(fontSize = action.size) }
                saveSettings("Tamaño de fuente actualizado")
            }
            is AISettingsAction.OnAvatarSourceChange -> {
                if (action.source == AvatarSource.GALLERY) {
                    _actionEvent.value = Event(AISettingsAction.OpenGallery)
                    _state.update { it.copy(avatarSource = action.source) }
                } else {
                    _state.update { it.copy(avatarSource = action.source) }
                    saveSettings("Fuente de avatar actualizada") 
                    updateAvatar(null, "Avatar removido") 
                }
            }
            is AISettingsAction.ShowToast -> {
                 _actionEvent.value = Event(action)
            }
            AISettingsAction.OpenGallery -> { }
        }
    }

    fun onAvatarSelected(uri: android.net.Uri, context: Context) {
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
                     
                     // If we removed avatar, clear local bitmap
                     if (avatar == null) {
                         _state.update { it.copy(avatarBitmap = null) }
                         // Also clear from repository
                         com.example.compose.geniatea.data.repository.AvatarRepository(getApplication()).deleteAvatar()
                     } else {
                         // If we updated avatar, we might want to save it to repository too?
                         // Ideally yes.
                         _state.value.avatarBitmap?.let {
                             com.example.compose.geniatea.data.repository.AvatarRepository(getApplication()).saveAvatar(it)
                         }
                     }
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
                         // Save to repo
                         com.example.compose.geniatea.data.repository.AvatarRepository(getApplication()).saveAvatar(bitmap)
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
                    language = currentState.language,
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
                    // Update Local Persistence using Application Context
                     val store = storeDataUser
                     store.savePictogramsEnabled(dto.showPictograms ?: false)
                     store.saveShowAvatar(dto.showAvatar ?: false)
                     store.saveResponseStyle(dto.responseStyle ?: "normal")
                     store.saveFontSize(dto.fontSize ?: "M")

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