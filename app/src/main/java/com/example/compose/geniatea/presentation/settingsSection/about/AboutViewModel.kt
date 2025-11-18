package com.example.compose.geniatea.presentation.settingsSection.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class AboutViewModel: ViewModel() {

    private val _actionEvent = MutableLiveData<Event<AboutAction>>()
    val actionEvent: LiveData<Event<AboutAction>> = _actionEvent

    fun onAction(action: AboutAction) {
        when (action) {
            AboutAction.OnBackPressed -> {
                _actionEvent.value = Event(AboutAction.OnBackPressed)
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