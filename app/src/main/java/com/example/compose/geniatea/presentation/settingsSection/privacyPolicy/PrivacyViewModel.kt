package com.example.compose.geniatea.presentation.settingsSection.privacyPolicy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class PrivacyViewModel: ViewModel() {

    private val _actionEvent = MutableLiveData<Event<PrivacyAction>>()
    val actionEvent: LiveData<Event<PrivacyAction>> = _actionEvent

    fun onAction(action: PrivacyAction) {
        when (action) {
            PrivacyAction.OnBackPressed -> {
                _actionEvent.value = Event(PrivacyAction.OnBackPressed)
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