package com.example.compose.geniatea.presentation.settingsSection.locationSettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow


class LocationViewModel: ViewModel() {

    private val _state = MutableStateFlow(LocationScreenState(initialLocations = listOf(
            Location(
                location = "123 Main St, Springfield, IL 62701",
                name = "Home"
            ),
            Location(
                location = "456 Elm St, Springfield, IL 62701",
                name = "Work",
            ),
            Location(
                location = "789 Oak St, Springfield, IL 62701",
                name = "Gym"
            ),
        )
    ))
    var state = _state

    private val _actionEvent = MutableLiveData<Event<LocationAction>>()
    val actionEvent: LiveData<Event<LocationAction>> = _actionEvent

    fun onAction(action: LocationAction) {
        when (action) {
            LocationAction.OnBackPressed -> {
                _actionEvent.value = Event(LocationAction.OnBackPressed)
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