package com.example.compose.geniatea.presentation.userManagementSection.preregister

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.compose.geniatea.R
import com.example.compose.geniatea.domain.User
import com.example.compose.geniatea.presentation.userManagementSection.login.Event
import com.example.compose.geniatea.utils.TextFielValidity
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException

class PreRegisterViewModel : ViewModel() {

    private val _state = MutableStateFlow(PreRegisterScreenState())
    val state = _state

    private val _navigationEvent = MutableLiveData<Event<PreRegisterAction>>()
    val navigationEvent: LiveData<Event<PreRegisterAction>> = _navigationEvent


    fun continueClicked(context: Context){
        _state.value = _state.value.copy(error = null)
        if(checkDataValidity()){
            viewModelScope.launch {
                try {
                  /* val response = BackendAPI.retrofitService.emailCheck(
                        ApiService.EmailCheckRequest(
                            email = state.value.email,
                        )
                    )

                    if (response.isSuccessful) {
                        Log.i("PreRegisterViewModel", "Server said: ${response.body()}")

                        val user = User(
                            email = state.value.email,
                        )
                        _navigationEvent.value = Event(PreRegisterAction.OnEmailSuccess(user))
                    } else {
                        _navigationEvent.value = Event(PreRegisterAction.OnEmailError("Email registration failed"))
                    }*/

                    val user = User(
                        email = state.value.email,
                    )
                    _navigationEvent.value = Event(PreRegisterAction.OnEmailSuccess(user))


                } catch (e: SocketTimeoutException) {
                    Toast.makeText(context, "Connection not available right now", Toast.LENGTH_SHORT).show()
                } catch (e: java.net.UnknownHostException) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Log.e("LoginViewModel", "Network error", e)
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                } catch (e: HttpException) {
                    Log.e("LoginViewModel", "Server error", e)
                    Toast.makeText(context, "Server error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkDataValidity(): Boolean {
        val currentState = state.value
        var isValid = true

        _state.value = currentState.copy(
            errorEmail = false,
        )

        if (currentState.email.isBlank()) {
            _state.value = _state.value.copy(errorEmail = true)
            _state.value = _state.value.copy(error = R.string.form_error_required_field_email)
        }

        if (TextFielValidity.isnotValidEmail(state.value.email)) {
            _state.value = _state.value.copy(error = R.string.form_error_invalid_email)
            isValid = false
        }

        return isValid
    }

    fun onAction(action: PreRegisterAction) {
        when (action) {
            is PreRegisterAction.OnContinueClicked -> {
                _navigationEvent.value = Event(PreRegisterAction.OnContinueClicked)
            }
            is PreRegisterAction.OnEmailChange -> {
                _state.value = _state.value.copy(email = action.email)
            }
            is PreRegisterAction.OnEmailSuccess -> {
                _navigationEvent.value = Event(PreRegisterAction.OnEmailSuccess(action.user))
            }
            is PreRegisterAction.OnEmailError -> {
                _navigationEvent.value = Event(PreRegisterAction.OnEmailError(action.error))
            }
        }
    }

}

