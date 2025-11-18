package com.example.compose.geniatea.presentation.settingsSection.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.StoreDataUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel: ViewModel() {

    private val _state = MutableStateFlow(SettingsScreenState())
    val state = _state
    private val _actionEvent = MutableLiveData<Event<SettingsAction>>()
    val actionEvent: LiveData<Event<SettingsAction>> = _actionEvent
    private val storeData = StoreDataUser()

    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode

    // Called when loading data on startup
    fun setDarkMode(isDark: Boolean) {
        _darkMode.value = isDark
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.OnBackPressed -> {
                _actionEvent.value = Event(SettingsAction.OnBackPressed)
            }
            SettingsAction.OnAccountPressed -> {
                _actionEvent.value = Event(SettingsAction.OnAccountPressed)
            }

            is SettingsAction.OnLogoutPressed -> {
                _actionEvent.value = Event(SettingsAction.OnLogoutPressed)
            }
            is SettingsAction.OnAboutPressed -> {
                _actionEvent.value = Event(SettingsAction.OnAboutPressed)
            }
            is SettingsAction.OnPrivacyPolicyPressed -> {
                _actionEvent.value = Event(SettingsAction.OnPrivacyPolicyPressed)
            }
            is SettingsAction.OnLocalizationsPressed -> {
                _actionEvent.value = Event(SettingsAction.OnLocalizationsPressed)
            }
            is SettingsAction.OnAISettingsPressed -> {
                _actionEvent.value = Event(SettingsAction.OnAISettingsPressed)
            }

            is SettingsAction.OnDarkModeToggle -> {
                _state.value = _state.value.copy(isDarkMode = !_state.value.isDarkMode)
                _actionEvent.value = Event(SettingsAction.OnDarkModeToggle(_state.value.isDarkMode))
            }

            is SettingsAction.OnAnimationsToggle -> {
                _state.value = _state.value.copy(isAnimationsEnabled = !_state.value.isAnimationsEnabled)
                _actionEvent.value = Event(SettingsAction.OnAnimationsToggle(_state.value.isAnimationsEnabled))
            }

            is SettingsAction.OnPictogramsToggle -> {
                _state.value = _state.value.copy(isPictosEnabled = !_state.value.isPictosEnabled)
                _actionEvent.value = Event(SettingsAction.OnPictogramsToggle(_state.value.isPictosEnabled))

                //TODO: Llamada al back o guardar en datastore
            }

            is SettingsAction.OnLanguagePress -> {
                _actionEvent.value = Event(SettingsAction.OnLanguagePress(action.language))
            }

            SettingsAction.OnNotificationPress -> {
                _actionEvent.value = Event(SettingsAction.OnNotificationPress)
            }

            is SettingsAction.OnAppIconPressed -> {
                _actionEvent.value = Event(SettingsAction.OnAppIconPressed)
            }

            is SettingsAction.OnAppColorPressed -> {
                _actionEvent.value = Event(SettingsAction.OnAppColorPressed)
            }

        }
    }

    suspend fun logoutUser(context: Context){
        storeData.logoutUser(context)
    }


    suspend fun setDarkMode(isDarkMode: Boolean, context: Context) {
        storeData.saveDarkMode(context, isDarkMode)
        _state.value = _state.value.copy(isDarkMode = isDarkMode)

        withContext(Dispatchers.Main) {
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    suspend fun setAnimationsEnabled(isEnabled: Boolean, context: Context) {
        storeData.saveAnimationsEnabled(context, isEnabled)
        _state.value = _state.value.copy(isAnimationsEnabled = isEnabled)
    }

    suspend fun setPictogramsEnabled(isEnabled: Boolean, context: Context) {
        storeData.savePictogramsEnabled(context, isEnabled)
        _state.value = _state.value.copy(isPictosEnabled = isEnabled)
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