package com.example.compose.geniatea.presentation.settingsSection.settings

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.data.backendConection.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(SettingsScreenState())
    val state = _state

    private val _actionEvent = MutableLiveData<Event<SettingsAction>>()
    val actionEvent: LiveData<Event<SettingsAction>> = _actionEvent

    // 2. Usamos el contexto de la aplicación
    private val storeData = StoreDataUser(getApplication<Application>().applicationContext)

    // Estado separado para el Toggle (si es necesario sincronizar con _state)
    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode

    // Función simple para inicializar estado visual si viene de otro lado
    fun setDarkModeState(isDark: Boolean) {
        _darkMode.value = isDark
    }

    private val _fontSize = MutableStateFlow("M")
    val fontSize: StateFlow<String> = _fontSize

    init {
       viewModelScope.launch {
           storeData.getFontSize().collect {
               _fontSize.value = it
           }
       }
        viewModelScope.launch {
            storeData.getContinuousVoiceMode().collect {
                _state.value = _state.value.copy(isContinuousVoiceEnabled = it)
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.OnBackPressed -> _actionEvent.value = Event(SettingsAction.OnBackPressed)
            SettingsAction.OnAccountPressed -> _actionEvent.value = Event(SettingsAction.OnAccountPressed)
            is SettingsAction.OnLogoutPressed -> {
                logoutUser() // Llamamos a la función de logout
                _actionEvent.value = Event(SettingsAction.OnLogoutPressed)
            }
            is SettingsAction.OnAboutPressed -> _actionEvent.value = Event(SettingsAction.OnAboutPressed)
            is SettingsAction.OnPrivacyPolicyPressed -> _actionEvent.value = Event(SettingsAction.OnPrivacyPolicyPressed)
            is SettingsAction.OnLocalizationsPressed -> _actionEvent.value = Event(SettingsAction.OnLocalizationsPressed)
            is SettingsAction.OnAISettingsPressed -> _actionEvent.value = Event(SettingsAction.OnAISettingsPressed)

            // --- Lógica de Toggles corregida para guardar datos ---

            is SettingsAction.OnDarkModeToggle -> {
                val newValue = !_state.value.isDarkMode
                // Actualizamos UI inmediata
                _state.value = _state.value.copy(isDarkMode = newValue)
                // Guardamos en persistencia
                saveDarkMode(newValue)
                _actionEvent.value = Event(SettingsAction.OnDarkModeToggle(newValue))
            }

            is SettingsAction.OnAnimationsToggle -> {
                val newValue = !_state.value.isAnimationsEnabled
                _state.value = _state.value.copy(isAnimationsEnabled = newValue)
                // Guardamos en persistencia
                setAnimationsEnabled(newValue)
                _actionEvent.value = Event(SettingsAction.OnAnimationsToggle(newValue))
            }

            is SettingsAction.OnContinuousVoiceToggle -> {
                val newValue = !_state.value.isContinuousVoiceEnabled
                _state.value = _state.value.copy(isContinuousVoiceEnabled = newValue)
                saveContinuousVoiceMode(newValue)
                _actionEvent.value = Event(SettingsAction.OnContinuousVoiceToggle(newValue))
            }



            is SettingsAction.OnLanguagePress -> {
                saveLanguagePreference(action.language)
                _actionEvent.value = Event(SettingsAction.OnLanguagePress(action.language))
            }
            SettingsAction.OnNotificationPress -> _actionEvent.value = Event(SettingsAction.OnNotificationPress)
            is SettingsAction.OnAppIconPressed -> _actionEvent.value = Event(SettingsAction.OnAppIconPressed)
            is SettingsAction.OnAppColorPressed -> _actionEvent.value = Event(SettingsAction.OnAppColorPressed)
        }
    }

    // 3. Funciones de lógica (Ya no necesitan recibir Context por parámetro)

    fun logoutUser() {
        viewModelScope.launch {
            storeData.logoutUser()
        }
    }

    // Renombrado a 'save' para diferenciar de la actualización de estado simple
    fun saveDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            storeData.saveDarkMode(isDarkMode)
            _darkMode.value = isDarkMode // Sincronizamos el otro flujo si se usa

            withContext(Dispatchers.Main) {
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
    }

    fun setAnimationsEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            storeData.saveAnimationsEnabled(isEnabled)
            // El estado _state ya se actualizó en onAction, pero por seguridad:
            _state.value = _state.value.copy(isAnimationsEnabled = isEnabled)
        }
    }

    fun saveContinuousVoiceMode(isEnabled: Boolean) {
        viewModelScope.launch {
            storeData.saveContinuousVoiceMode(isEnabled)
            _state.value = _state.value.copy(isContinuousVoiceEnabled = isEnabled)
        }
    }



    fun getUserData() {
        viewModelScope.launch {
            val id = storeData.getId() ?: return@launch
            try {
                val response = BackendAPI.retrofitService.getUserById(id)
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        _state.value = _state.value.copy(
                            name = user.name,
                            username = user.username
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getLanguageCode(language: String): String {
        return when (language) {
            "Español" -> "spanish"
            "English" -> "english"
            "Français" -> "french"
            else -> "spanish"
        }
    }

    private fun saveLanguagePreference(languageName: String) {
        val languageCode = getLanguageCode(languageName)
        android.util.Log.d("LanguageUpdate", "Updating language. Name: $languageName, Code: $languageCode")
        viewModelScope.launch {
            val token = storeData.getToken() ?: return@launch
            try {
                // Create partial DTO for language update only
                val partialPrefs = ApiService.UserPreferenceDTO(
                    showPictograms = null,
                    language = languageCode,
                    showAvatar = null,
                    clearLanguage = null,
                    responseStyle = null,
                    fontSize = null,
                    avatarUrl = null,
                    avatarVideo = null
                )

                val response = BackendAPI.retrofitService.updatePreference("Bearer $token", partialPrefs)
                if (response.isSuccessful) {
                    storeData.saveLanguage(languageCode)
                }
            } catch (e: Exception) {
                e.printStackTrace()
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