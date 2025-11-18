package com.example.compose.geniatea.presentation.settingsSection.aiSettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class AISettingsViewModel: ViewModel() {

    private val _actionEvent = MutableLiveData<Event<AISettingsAction>>()
    val actionEvent: LiveData<Event<AISettingsAction>> = _actionEvent

    fun onAction(action: AISettingsAction) {
        when (action) {
            AISettingsAction.OnBackPressed -> {
                _actionEvent.value = Event(AISettingsAction.OnBackPressed)
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