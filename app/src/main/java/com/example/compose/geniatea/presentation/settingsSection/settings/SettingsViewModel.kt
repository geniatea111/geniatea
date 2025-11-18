package com.example.compose.geniatea.presentation.settingsSection.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.compose.geniatea.data.StoreDataUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class SettingsViewModel: ViewModel() {

    private val _state = MutableStateFlow(SettingsScreenState())
    val state = _state
    private val _actionEvent = MutableLiveData<Event<SettingsAction>>()
    val actionEvent: LiveData<Event<SettingsAction>> = _actionEvent
    private val storeData = StoreDataUser()

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
            is SettingsAction.OnPreferencesPressed -> {
                _actionEvent.value = Event(SettingsAction.OnPreferencesPressed)
            }

        }
    }

    suspend fun logoutUser(context: Context){
        storeData.logoutUser(context)
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