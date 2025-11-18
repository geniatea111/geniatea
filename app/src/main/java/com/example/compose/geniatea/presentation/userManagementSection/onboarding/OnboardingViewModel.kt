package com.example.compose.geniatea.presentation.userManagementSection.onboarding

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardingViewModel : ViewModel() {
    private val _navigationEvent = MutableLiveData<Event<OnboardingAction>>()
    val navigationEvent: LiveData<Event<OnboardingAction>> = _navigationEvent

    var state by mutableStateOf(OnboardingScreenState())
        private set


    fun updateImage(uri: Uri?) {
        state = state.copy(selectedImage = uri)
    }

    fun updateOption(option: String) {
        state = state.copy(selectedOption = option)
    }

    fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.OnBackPressed -> {
                _navigationEvent.value = Event(OnboardingAction.OnBackPressed)
            }
            is OnboardingAction.OnSkipPressed -> {
                _navigationEvent.value = Event(OnboardingAction.OnSkipPressed)
            }
            is OnboardingAction.OnContinuePressed -> {
                _navigationEvent.value = Event(OnboardingAction.OnContinuePressed)
            }
            is OnboardingAction.OnOptionSelectedPerson -> {
                updateOption(action.option)
            }
            is OnboardingAction.OnOptionSelectedGoal -> {
                updateOption(action.option)
            }
            is OnboardingAction.OnGenerateImagePressed -> {
                _navigationEvent.value = Event(OnboardingAction.OnGenerateImagePressed)
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


