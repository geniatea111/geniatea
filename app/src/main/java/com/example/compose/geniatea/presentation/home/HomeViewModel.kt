package com.example.compose.geniatea.presentation.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.data.Conversation
import com.example.compose.geniatea.data.backendConection.ApiService
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.presentation.funcionalidades.tasklist.TaskDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel() : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()
    private val _navigationEvent = MutableLiveData<Event<HomeAction>>()
    val navigationEvent: LiveData<Event<HomeAction>> = _navigationEvent

    init {
        getRecentConversations()
        getPendingTasks()
    }

    private fun getRecentConversations() {
        viewModelScope.launch {
            try {
                val token = ""
                val response = BackendAPI.retrofitService.getChatSessions(token)
                if (response.isSuccessful) {
                    val conversations = response.body()?.map { 
                        Conversation(it.sessionId, it.topic, System.currentTimeMillis()) 
                    } ?: emptyList()
                    _state.update { it.copy(conversations = conversations.take(2)) }
                } else {
                    // Handle error
                    Log.e("HomeViewModel", "Error getting conversations: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                // Handle exception
                Log.e("HomeViewModel", "Exception getting conversations", e)
            }
        }
    }

    private fun getPendingTasks() {
        viewModelScope.launch {
            try {
                val response = BackendAPI.retrofitService.getPendingTasks()
                if (response.isSuccessful) {
                    val tasks = response.body()?.map { it.title } ?: emptyList()
                    _state.update { it.copy(pendingTasks = tasks) }
                } else {
                    Log.e("HomeViewModel", "Error getting tasks: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception getting tasks", e)
            }
        }
    }

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

            is HomeAction.OnRecentChatPressed -> {
                _navigationEvent.value = Event(action)
            }

            HomeAction.OnSeeAllRecentChatsPressed -> {
                _navigationEvent.value = Event(HomeAction.OnSeeAllRecentChatsPressed)
            }
        }
    }

    suspend fun setData(context: Context) {
        val name = StoreDataUser(context).getName() ?: "Usuario"
        val greeting = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
            in 0..13 -> "Buenos días"
            in 14..20 -> "Buenas tardes"
            else -> "Buenas noches"
        }
        _state.update { it.copy(saludo = "$greeting, $name") }
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
