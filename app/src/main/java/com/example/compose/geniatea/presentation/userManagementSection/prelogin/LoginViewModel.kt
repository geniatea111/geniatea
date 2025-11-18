package com.example.compose.geniatea.presentation.userManagementSection.prelogin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PreloginViewModel : ViewModel() {
    private val _navigationEvent = MutableLiveData<Event<PreloginAction>>()
    val navigationEvent: LiveData<Event<PreloginAction>> = _navigationEvent


    fun onAction(action: PreloginAction) {
        when (action) {
            is PreloginAction.OnBackPressed -> {
                _navigationEvent.value = Event(PreloginAction.OnBackPressed)
            }
            is PreloginAction.OnLoginPressed -> {
                _navigationEvent.value = Event(PreloginAction.OnLoginPressed)
            }
            is PreloginAction.OnRegisterPressed -> {
                _navigationEvent.value = Event(PreloginAction.OnRegisterPressed)
            }
            is PreloginAction.OnTermsOfServiceClicked -> {
                _navigationEvent.value = Event(PreloginAction.OnTermsOfServiceClicked)
            }
            is PreloginAction.OnPrivacyPolicyClicked -> {
                _navigationEvent.value = Event(PreloginAction.OnPrivacyPolicyClicked)
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

    fun peekContent(): T = content
}


