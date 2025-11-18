package com.example.compose.geniatea.presentation.settingsSection.appIcon

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.compose.geniatea.theme.AppColorVariant


class  AppIconViewModel: ViewModel() {

    private val _actionEvent = MutableLiveData<Event< AppIconAction>>()
    val actionEvent: LiveData<Event<AppIconAction>> = _actionEvent

    fun onAction(action: AppIconAction) {
        when (action) {
            AppIconAction.OnBackPressed -> {
                _actionEvent.value = Event(AppIconAction.OnBackPressed)
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