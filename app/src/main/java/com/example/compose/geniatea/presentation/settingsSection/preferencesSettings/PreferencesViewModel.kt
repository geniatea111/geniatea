package com.example.compose.geniatea.presentation.settingsSection.preferencesSettings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.presentation.settingsSection.settings.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext


class PreferencesViewModel: ViewModel() {

    private val _state = MutableStateFlow(PreferencesScreenState())
    val state = _state

    private val _actionEvent = MutableLiveData<Event<PreferencesAction>>()
    val actionEvent: LiveData<Event<PreferencesAction>> = _actionEvent
    private val storeData = StoreDataUser()

    fun onAction(action: PreferencesAction) {
        when (action) {
            PreferencesAction.OnBackPressed -> {
                _actionEvent.value = Event(PreferencesAction.OnBackPressed)
            }

            is PreferencesAction.OnDarkModeToggle -> {
                _state.value = _state.value.copy(isDarkMode = !_state.value.isDarkMode)
                _actionEvent.value = Event(PreferencesAction.OnDarkModeToggle(_state.value.isDarkMode))
            }

            is PreferencesAction.OnLanguagePress -> {
                _actionEvent.value = Event(PreferencesAction.OnLanguagePress(action.language))
            }

            PreferencesAction.OnNotificationPress -> {
               _actionEvent.value = Event(PreferencesAction.OnNotificationPress)
            }
        }
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