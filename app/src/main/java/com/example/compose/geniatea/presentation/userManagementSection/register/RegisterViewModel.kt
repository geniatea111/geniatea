package com.example.compose.geniatea.presentation.userManagementSection.register

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.data.backendConection.ApiService
import com.example.compose.geniatea.domain.User
import com.example.compose.geniatea.presentation.userManagementSection.login.Event
import com.example.compose.geniatea.utils.TextFielValidity
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException

class RegisterViewModel : ViewModel() {

    private val _state = MutableStateFlow(RegisterScreenState())
    val state = _state

    private val _navigationEvent = MutableLiveData<Event<RegisterAction>>()
    val navigationEvent: LiveData<Event<RegisterAction>> = _navigationEvent


    fun register(context: Context){
        _state.value = _state.value.copy(errorForm = null)

        if(checkDataValidity()){
            viewModelScope.launch {
                try {
                    val response = BackendAPI.retrofitService.register(
                        ApiService.RegisterRequest(
                         //   name = state.value.name,
                            username = state.value.username,
                            email = state.value.email,
//                            birthdate = formatDateToBack(state.value.birthDate),
//                            gender = formatGenderToBack(state.value.gender),
                            password = state.value.password,
                            roles = listOf("USER")
                        )
                    )

                    if (response.isSuccessful) {
                        Log.i("RegisterViewModel", "Server said: ${response.body()}")

                        val user = User(
                            id = response.body()?.id ?: 0L,
                            accessToken = response.body()?.accessToken ?: "",
                            refreshToken = response.body()?.refreshToken ?: "",
                            name = response.body()?.name ?: "",
                        )
                        _navigationEvent.value = Event(RegisterAction.OnRegisterSuccess(user))
                    } else {
                        _navigationEvent.value = Event(RegisterAction.OnRegisterError("Registration failed"))
                    }


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
                    _state.value = _state.value.copy(error = "Server error: ${e.message()}")
                }
            }
        }

        // Here you would typically call your backend API to register the user
    }

    private fun checkDataValidity(): Boolean {
        val currentState = state.value
        var isValid = true

        _state.value = currentState.copy(
          //  errorName = false,
            errorUsername = false,
            errorEmail = false,
//            errorBirthDate = false,
//            errorGender = false,
            errorPassword = false,
            errorConfirmPassword = false,
            errorForm = null
        )

        if (!isBlank(currentState)) {
            _state.value = _state.value.copy(errorForm = R.string.form_error_required_fields)
        }

        if (TextFielValidity.isnotValidEmail(state.value.email)) {
            _state.value = _state.value.copy(errorForm = R.string.form_error_invalid_email)
            isValid = false
        }


        if (TextFielValidity.isnotValidPassword(state.value.password)) {
            _state.value = _state.value.copy(errorForm = R.string.form_error_password_strength)
            isValid = false
        }

        if (TextFielValidity.notsamePasswords(state.value.password, state.value.confirmPassword)) {
            _state.value = _state.value.copy(errorForm = R.string.form_error_password_mismatch)
            isValid = false
        }

        return isValid
    }

    fun isBlank(currentState : RegisterScreenState): Boolean {
        var isValid = true
//        if (currentState.name.isBlank()) {
//            _state.value = _state.value.copy(errorName = true)
//            isValid = false
//        }

        if (currentState.username.isBlank()) {
            _state.value = _state.value.copy(errorUsername = true)
            isValid = false
        }

        if (currentState.email.isBlank()) {
            _state.value = _state.value.copy(errorEmail = true)
            isValid = false
        }

//        if (currentState.birthDate.isBlank()) {
//            _state.value = _state.value.copy(errorBirthDate = true)
//            isValid = false
//        }
//
//        if (currentState.gender.isBlank()){
//            _state.value = _state.value.copy(errorGender = true)
//            isValid = false
//        }

        if (currentState.password.isBlank()) {
            _state.value = _state.value.copy(errorPassword = true)
            isValid = false
        }

        if (currentState.confirmPassword.isBlank()) {
            _state.value = _state.value.copy(errorConfirmPassword = true)
            isValid = false
        }

        return isValid
    }


    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnRegisterClicked -> {
                _navigationEvent.value = Event(RegisterAction.OnRegisterClicked)
            }
//            is RegisterAction.OnNameChange -> {
//                _state.value = _state.value.copy(name = action.name)
//            }
            is RegisterAction.OnUsernameChange -> {
                _state.value = _state.value.copy(username = action.username)
            }
            is RegisterAction.OnEmailChange -> {
                _state.value = _state.value.copy(email = action.email)
            }
//            is RegisterAction.OnBirthDateChange -> {
//                _state.value = _state.value.copy(birthDate = action.birthDate)
//            }
//            is RegisterAction.OnGenderChange -> {
//                _state.value = _state.value.copy(gender = action.gender)
//            }
            is RegisterAction.OnPasswordChange -> {
                _state.value = _state.value.copy(password = action.password)
            }
            is RegisterAction.OnConfirmPasswordChange -> {
                _state.value = _state.value.copy(confirmPassword = action.confirmPassword)
            }
            is RegisterAction.OnRegisterSuccess -> {
                _navigationEvent.value = Event(RegisterAction.OnRegisterSuccess(action.user))
            }
            is RegisterAction.OnRegisterError -> {
                _navigationEvent.value = Event(RegisterAction.OnRegisterError(action.error))
            }
        }
    }

}

