package com.example.compose.geniatea.presentation.settingsSection.changePass

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.data.backendConection.ApiService
import com.example.compose.geniatea.data.StoreDataUser
import kotlinx.coroutines.flow.MutableStateFlow


class ChangePassViewModel: ViewModel() {

    private val _state = MutableStateFlow(ChangePassScreenState())
    val state = _state

    private val _actionEvent = MutableLiveData<Event<ChangePassAction>>()
    val actionEvent: LiveData<Event<ChangePassAction>> = _actionEvent

    fun onAction(action: ChangePassAction) {
        when (action) {
            ChangePassAction.OnChangePassClicked -> {
                _actionEvent.value = Event(ChangePassAction.OnChangePassClicked)
            }
            ChangePassAction.OnBackPressed -> {
                _actionEvent.value = Event(ChangePassAction.OnBackPressed)
            }
            is ChangePassAction.OnNewPassChange -> {
                _state.value = _state.value.copy(newPassword = action.newPass)
            }
            is ChangePassAction.OnCurrentPassChange -> {
                _state.value = _state.value.copy(currentPassword = action.currentPass)
            }
            is ChangePassAction.OnConfirmNewPassChange -> {
                _state.value = _state.value.copy(confirmNewPassword = action.confirmNewPass)
            }
        }
    }

    suspend fun changePassword(context: Context){
        val response = BackendAPI.retrofitService.updatePassword(
            token = "Bearer ${StoreDataUser().getToken(context)}",
            ApiService.UpdatePasswordRequest(
                currentPassword = state.value.currentPassword,
                newPassword = state.value.newPassword
            )
        )

        if (response.isSuccessful) {
            _actionEvent.value = Event(ChangePassAction.OnChangePassSuccess)
        } else {
            _actionEvent.value = Event(ChangePassAction.OnChangePassError("Error al cambiar la contraseña"))
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