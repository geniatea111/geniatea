package com.example.compose.geniatea.presentation.funcionalidades.resourcestea

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ResourcesViewModel: ViewModel() {

    val _state = MutableStateFlow(ResourcesScreenState(
        content = "",
    ))
    
    var state = _state
    private val _actionEvent = MutableLiveData<Event<ResourcesAction>>()
    val navigationEvent: LiveData<Event<ResourcesAction>> = _actionEvent

    fun onAction(action: ResourcesAction) {
        when (action) {
            ResourcesAction.OnBackPressed -> {
                _actionEvent.value = Event(ResourcesAction.OnBackPressed)
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