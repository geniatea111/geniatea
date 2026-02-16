package com.example.compose.geniatea.presentation.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.data.Conversation
import com.example.compose.geniatea.data.backendConection.ApiService
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.presentation.funcionalidades.tasklist.TaskDTO
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel() : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()
    private val _navigationEvent = MutableLiveData<Event<HomeAction>>()
    val navigationEvent: LiveData<Event<HomeAction>> = _navigationEvent

    init {
        getRecentConversations()
        getPendingTasks()
    }

    private fun getRecentConversations() {
        viewModelScope.launch {
            try {
                val token = ""
                val response = BackendAPI.retrofitService.getChatSessions(token)
                if (response.isSuccessful) {
                    val conversations = response.body()?.map { 
                        Conversation(it.sessionId, it.topic, System.currentTimeMillis()) 
                    } ?: emptyList()
                    _state.update { it.copy(conversations = conversations.take(2)) }
                } else {
                    // Handle error
                    Log.e("HomeViewModel", "Error getting conversations: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                // Handle exception
                Log.e("HomeViewModel", "Exception getting conversations", e)
            }
        }
    }

    private fun getPendingTasks() {
        viewModelScope.launch {
            try {
                val response = BackendAPI.retrofitService.getPendingTasks()
                if (response.isSuccessful) {
                    val tasks = response.body()?.map { it.title } ?: emptyList()
                    _state.update { it.copy(pendingTasks = tasks) }
                } else {
                    Log.e("HomeViewModel", "Error getting tasks: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception getting tasks", e)
            }
        }
    }

    fun refreshData() {
        getRecentConversations()
        getPendingTasks()
    }

    private fun fetchParameters(context: Context) {
        viewModelScope.launch {
            val store = StoreDataUser(context)
            val token = store.getToken() ?: return@launch
            
            // Fetch User Preferences
            try {
                val response = BackendAPI.retrofitService.getUserPreferences("Bearer $token")
                if (response.isSuccessful) {
                    val prefs = response.body()
                    if (prefs != null) {
                        store.savePictogramsEnabled(prefs.showPictograms ?: false)
                        
                        prefs.language?.let { rawLanguage ->
                            store.saveLanguage(rawLanguage)
                            val language = rawLanguage.trim().lowercase(Locale.ROOT)
                            
                            Log.d("HomeViewModel", "Backend language: $rawLanguage, Normalized: $language")
                            
                            val tag = when (language) {
                                "español", "spanish", "es" -> "es"
                                "english", "ingles", "en" -> "en"
                                "français", "french", "fr" -> "fr"
                                else -> "es"
                            }
                            
                            val appLocale = LocaleListCompat.create(Locale.forLanguageTag(tag))
                            val currentLocales = AppCompatDelegate.getApplicationLocales()
                            val currentTag = if (!currentLocales.isEmpty) currentLocales.get(0)?.language else "es"
                            
                            Log.d("HomeViewModel", "Current Tag: $currentTag, New Tag: $tag")
                            
                            if (currentTag != tag) {
                                Log.d("HomeViewModel", "Applying new locale: $tag")
                                AppCompatDelegate.setApplicationLocales(appLocale)
                            } else {
                                Log.d("HomeViewModel", "Locale already set to $tag")
                            }
                        }
                        store.saveShowAvatar(prefs.showAvatar ?: false)
                        
                        // Map response style
                        val style = when(prefs.responseStyle) {
                            "concise" -> "concise"
                            "learning" -> "learning"
                            else -> "normal"
                        }
                        store.saveResponseStyle(style)

                        // Map font size
                        val size = when(prefs.fontSize) {
                            "S" -> "S"
                            "L" -> "L"
                            else -> "M"
                        }
                        store.saveFontSize(size)
                        
                        // Process Avatar fetching if needed
                        if (prefs.showAvatar == true) {
                             fetchAndSaveAvatar(context, token, store.getId() ?: 0L)
                             if (!prefs.avatarVideo.isNullOrEmpty()) {
                                 fetchAndSaveAvatarVideo(context, token, store.getId() ?: 0L)
                             }
                        } else {
                             // Correctly handle case where avatar is disabled/removed?
                             // Maybe delete local avatar? user preferences say showAvatar=false
                             // so we might not need to delete, just not show. 
                             // But if they switched to Geni, showAvatar would be false (gallery is false).
                             // Let's rely on showAvatar boolean for UI.
                        }

                        // Sync voice settings
                        if (prefs.continuousVoice != null) {
                            store.saveContinuousVoiceMode(prefs.continuousVoice)
                        }
                        if (prefs.voiceKeyword != null) {
                            store.saveContinuousVoiceKeyword(prefs.voiceKeyword)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception fetching preferences", e)
            }
        }
    }

    private suspend fun fetchAndSaveAvatar(context: Context, token: String, userId: Long) {
         try {
            val response = BackendAPI.retrofitService.getAvatar("Bearer $token", userId)
            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                if (bytes != null) {
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    com.example.compose.geniatea.data.repository.AvatarRepository(context).saveAvatar(bitmap)
                }
            }
        } catch (e: Exception) {
             Log.e("HomeViewModel", "Exception fetching avatar", e)
        }
    }

    private suspend fun fetchAndSaveAvatarVideo(context: Context, token: String, userId: Long) {
         try {
            val response = BackendAPI.retrofitService.getAvatarVideo("Bearer $token", userId)
            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                if (bytes != null) {
                    // Save to temp file to create URI
                    val tempFile = java.io.File.createTempFile("avatar_video_temp", ".mp4", context.cacheDir)
                    tempFile.writeBytes(bytes)
                    val uri = android.net.Uri.fromFile(tempFile)
                    // Save to persistent storage
                    com.example.compose.geniatea.data.repository.AvatarRepository(context).saveAvatarVideo(uri)
                }
            }
        } catch (e: Exception) {
             Log.e("HomeViewModel", "Exception fetching avatar video", e)
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnBackPressed -> {
                _navigationEvent.value = Event(HomeAction.OnBackPressed)
            }

            HomeAction.OnAccountPressed -> {
                _navigationEvent.value = Event(HomeAction.OnAccountPressed)
            }

            HomeAction.OnChatPressed -> {
                _navigationEvent.value = Event(HomeAction.OnChatPressed)
            }

            HomeAction.OnFormalizerPressed -> {
                _navigationEvent.value = Event(HomeAction.OnFormalizerPressed)
            }

            HomeAction.OnJudgePressed -> {
                _navigationEvent.value = Event(HomeAction.OnJudgePressed)
            }

            HomeAction.OnResourcesPressed -> {
                _navigationEvent.value = Event(HomeAction.OnResourcesPressed)
            }

            HomeAction.OnTaskListPressed -> {
                _navigationEvent.value = Event(HomeAction.OnTaskListPressed)
            }

            is HomeAction.OnRecentChatPressed -> {
                _navigationEvent.value = Event(action)
            }

            HomeAction.OnSeeAllRecentChatsPressed -> {
                _navigationEvent.value = Event(HomeAction.OnSeeAllRecentChatsPressed)
            }

            HomeAction.OnFavoritesPressed -> {
                _navigationEvent.value = Event(HomeAction.OnFavoritesPressed)
            }
        }
    }

    suspend fun setData(context: Context) {
        fetchParameters(context)
        val name = StoreDataUser(context).getName() ?: "Usuario"
        val greeting = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
            in 0..13 -> "Buenos días"
            in 14..20 -> "Buenas tardes"
            else -> "Buenas noches"
        }
        _state.update { it.copy(saludo = "$greeting, $name") }
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
