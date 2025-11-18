package com.example.compose.geniatea.presentation.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.compose.geniatea.data.StoreDataUser
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel: ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state
    private val _navigationEvent = MutableLiveData<Event<HomeAction>>()
    val navigationEvent: LiveData<Event<HomeAction>> = _navigationEvent

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnBackPressed -> {
                _navigationEvent.value = Event(HomeAction.OnBackPressed)
            }
            HomeAction.OnAccountPressed -> {
                _navigationEvent.value = Event(HomeAction.OnAccountPressed)
            }
            HomeAction.OnChatPressed -> {
                _navigationEvent.value = Event(HomeAction.OnChatPressed)
            }

            HomeAction.OnFormalizerPressed -> {
                _navigationEvent.value = Event(HomeAction.OnFormalizerPressed)
            }
            HomeAction.OnJudgePressed -> {
                _navigationEvent.value = Event(HomeAction.OnJudgePressed)
            }
            HomeAction.OnResourcesPressed -> {
                _navigationEvent.value = Event(HomeAction.OnResourcesPressed)
            }
            HomeAction.OnTaskListPressed -> {
                _navigationEvent.value = Event(HomeAction.OnTaskListPressed)
            }
        }
    }

    suspend fun setData(context: Context) {
        val name = StoreDataUser().getName(context) ?: "Usuario"
        // dependiendo de la hora buenos dias, bueas tardes o buenas noches
        val greeting = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
            in 0..13 -> "Buenos días"
            in 14..20 -> "Buenas tardes"
            else -> "Buenas noches"
        }
        _state.value = _state.value.copy(saludo = "$greeting, $name")
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