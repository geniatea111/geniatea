package com.example.compose.geniatea.presentation.settingsSection.aiSettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class AISettingsViewModel: ViewModel() {

    private val _state = MutableStateFlow(AISettingsState())
    val state = _state.asStateFlow()

    private val _actionEvent = MutableLiveData<Event<AISettingsAction>>()
    val actionEvent: LiveData<Event<AISettingsAction>> = _actionEvent

    fun onAction(action: AISettingsAction) {
        when (action) {
            AISettingsAction.OnBackPressed -> {
                _actionEvent.value = Event(AISettingsAction.OnBackPressed)
            }
            is AISettingsAction.OnClearLanguageToggle -> {
                _state.value = _state.value.copy(isClearLanguage = action.isChecked)
            }
            is AISettingsAction.OnResponseStyleChange -> {
                _state.value = _state.value.copy(responseStyle = action.value)
            }
            is AISettingsAction.OnFontSizeChange -> {
                _state.value = _state.value.copy(fontSize = action.size)
            }
            is AISettingsAction.OnAvatarSourceChange -> {
                _state.value = _state.value.copy(avatarSource = action.source)
            }
            AISettingsAction.OnSavePressed -> {
                // TODO: Save to backend or local storage
                _actionEvent.value = Event(AISettingsAction.OnSavePressed)
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