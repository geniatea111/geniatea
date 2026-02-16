package com.example.compose.geniatea.presentation.settingsSection.aiSettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first


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
                                avatarSource = if (DTO.showAvatar == true) {
                                    if (!DTO.avatarVideo.isNullOrEmpty()) AvatarSource.VIDEO_GALLERY
                                    else AvatarSource.GALLERY
                                } else AvatarSource.GENI,
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

                        // Load local preferences for continuous voice
                        val continuousVoiceEnabled = store.getContinuousVoiceMode().first()
                        val continuousVoiceKeyword = store.getContinuousVoiceKeyword().first()
                        
                        _state.update { 
                            it.copy(
                                isContinuousVoiceEnabled = continuousVoiceEnabled,
                                continuousVoiceKeyword = continuousVoiceKeyword
                            ) 
                        }

                        if (DTO.showAvatar == true) {
                            if (!DTO.avatarVideo.isNullOrEmpty()) {
                                fetchAvatarVideo()
                            } else {
                                fetchAvatar()
                            }
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
                saveSettings(ApiService.UserPreferenceDTO(clearLanguage = action.isChecked, showPictograms = null, language = null, showAvatar = null, responseStyle = null, fontSize = null), "Lenguaje claro actualizado")
            }
            is AISettingsAction.OnShowPictogramsToggle -> {
                _state.update { it.copy(showPictograms = action.isChecked) }
                saveSettings(ApiService.UserPreferenceDTO(showPictograms = action.isChecked, language = null, showAvatar = null, clearLanguage = null, responseStyle = null, fontSize = null), "Pictogramas actualizados")
            }
            is AISettingsAction.OnResponseStyleChange -> {
                _state.update { it.copy(responseStyle = action.value) }
                val styleString = when (action.value) {
                    0f -> "concise"
                    1f -> "normal"
                    2f -> "learning"
                    else -> "normal"
                }
                saveSettings(ApiService.UserPreferenceDTO(responseStyle = styleString, showPictograms = null, language = null, showAvatar = null, clearLanguage = null, fontSize = null), "Estilo de respuesta actualizado")
            }
            is AISettingsAction.OnFontSizeChange -> {
                _state.update { it.copy(fontSize = action.size) }
                val sizeString = when (action.size) {
                    0 -> "S"
                    1 -> "M"
                    2 -> "L"
                    else -> "M"
                }
                saveSettings(ApiService.UserPreferenceDTO(fontSize = sizeString, showPictograms = null, language = null, showAvatar = null, clearLanguage = null, responseStyle = null), "Tamaño de fuente actualizado")
            }
            is AISettingsAction.OnAvatarSourceChange -> {
                if (action.source == AvatarSource.GALLERY) {
                    _actionEvent.value = Event(AISettingsAction.OpenGallery)
                    // Optimistically set source, though ideally wait for result
                    _state.update { it.copy(avatarSource = action.source) }
                } else if (action.source == AvatarSource.VIDEO_GALLERY) {
                    _actionEvent.value = Event(AISettingsAction.OpenVideoGallery)
                    _state.update { it.copy(avatarSource = action.source) }
                } else {
                    _state.update { it.copy(avatarSource = action.source) }
                    
                    // Remove both avatar and video
                    updateAvatar(null, "Avatar removido") 
                    updateAvatarVideo(null, "Video de avatar removido")

                    // Also update preference about showAvatar = false (generic)
                    saveSettings(ApiService.UserPreferenceDTO(showAvatar = false, showPictograms = null, language = null, clearLanguage = null, responseStyle = null, fontSize = null), "Fuente de avatar actualizada")
                }
            }
            is AISettingsAction.ShowToast -> {
                 _actionEvent.value = Event(action)
            }
            AISettingsAction.OpenGallery -> { }
            AISettingsAction.OpenVideoGallery -> { }
            is AISettingsAction.OnContinuousVoiceToggle -> {
                _state.update { it.copy(isContinuousVoiceEnabled = action.enabled) }
                viewModelScope.launch {
                    storeDataUser.saveContinuousVoiceMode(action.enabled)
                }
            }
            is AISettingsAction.OnKeywordChange -> {
                _state.update { it.copy(continuousVoiceKeyword = action.keyword) }
                viewModelScope.launch {
                    storeDataUser.saveContinuousVoiceKeyword(action.keyword)
                }
            }
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

    fun onAvatarVideoSelected(uri: android.net.Uri, context: Context) {
        val contentResolver = context.contentResolver
        val type = contentResolver.getType(uri) ?: "video/*"
        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()

        if (bytes != null) {
            val requestFile = okhttp3.RequestBody.create(
                type.toMediaTypeOrNull(),
                bytes
            )
            val body = okhttp3.MultipartBody.Part.createFormData("avatarVideo", "avatar_video.mp4", requestFile)
            updateAvatarVideo(body, "Video de avatar actualizado")

            // For video, we might want to generate a thumbnail or just indicate video is selected
            // For now, let's just update the source. 
            // Ideally we'd decode a frame as bitmap for preview.
            // But let's keep it simple first.
            // For video, we might want to generate a thumbnail or just indicate video is selected
            // For now, let's just update the source. 
            // Ideally we'd decode a frame as bitmap for preview.
            // But let's keep it simple first.
            _state.update { it.copy(avatarSource = AvatarSource.VIDEO_GALLERY, avatarVideoUri = uri) }
            // Maybe clear image bitmap to avoid confusion?
            // _state.update { it.copy(avatarBitmap = null) } 
        } else {
            Log.e("AISettingsViewModel", "Could not read video file from Uri")
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

    private fun updateAvatarVideo(video: okhttp3.MultipartBody.Part?, message: String) {
         val token = userToken ?: return
         val uId = userId ?: return

         viewModelScope.launch {
              try {
                  val response = BackendAPI.retrofitService.updateAvatarVideo("Bearer $token", uId, video)
                  if (response.isSuccessful) {
                      _actionEvent.value = Event(AISettingsAction.ShowToast(message))
                      // Save video locally if we have the URI in state (which we set in onAvatarVideoSelected)
                      _state.value.avatarVideoUri?.let { uri ->
                          com.example.compose.geniatea.data.repository.AvatarRepository(getApplication()).saveAvatarVideo(uri)
                      }
                  } else {
                      Log.e("AISettingsViewModel", "Error updating avatar video: ${response.code()}")
                  }
              } catch (e: Exception) {
                  Log.e("AISettingsViewModel", "Exception updating avatar video", e)
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

    private fun fetchAvatarVideo() {
        val token = userToken ?: return
        val uId = userId ?: return

        viewModelScope.launch {
            try {
                val response = BackendAPI.retrofitService.getAvatarVideo("Bearer $token", uId)
                if (response.isSuccessful) {
                    val bytes = response.body()?.bytes()
                    if (bytes != null) {
                        // Save to temp file to create URI, then save to repo
                        val context = getApplication<android.app.Application>().applicationContext
                        val tempFile = java.io.File.createTempFile("avatar_video_temp", ".mp4", context.cacheDir)
                        tempFile.writeBytes(bytes)
                        val uri = android.net.Uri.fromFile(tempFile)
                        
                        _state.update { it.copy(avatarVideoUri = uri) }
                        
                        // Save to repo for persistent access
                        com.example.compose.geniatea.data.repository.AvatarRepository(getApplication()).saveAvatarVideo(uri)
                    }
                } else {
                    Log.e("AISettingsViewModel", "Error fetching avatar video: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AISettingsViewModel", "Exception fetching avatar video", e)
            }
        }
    }


    private fun saveSettings(dto: ApiService.UserPreferenceDTO, changeMessage: String) {
        val token = userToken ?: return
        
        viewModelScope.launch {
            try {
                val response = BackendAPI.retrofitService.updatePreference("Bearer $token", dto)
                if (response.isSuccessful) {
                    // Update Local Persistence using Application Context
                     val store = storeDataUser
                     dto.showPictograms?.let { store.savePictogramsEnabled(it) }
                     dto.showAvatar?.let { store.saveShowAvatar(it) }
                     dto.responseStyle?.let { store.saveResponseStyle(it) }
                     dto.fontSize?.let { store.saveFontSize(it) }

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