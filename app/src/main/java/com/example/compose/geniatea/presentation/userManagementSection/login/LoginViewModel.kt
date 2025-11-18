package com.example.compose.geniatea.presentation.userManagementSection.login

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.data.backendConection.ApiService
import com.example.compose.geniatea.domain.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import retrofit2.HttpException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import java.net.SocketTimeoutException

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginScreenState())
    val state = _state
    private val _navigationEvent = MutableLiveData<Event<LoginAction>>()
    val navigationEvent: LiveData<Event<LoginAction>> = _navigationEvent

    fun login(context: Context){
        Log.i("LoginViewModel", "Login button clicked with state: ${state.value}")
        if(checkDataValidity()){
            viewModelScope.launch {
                try {
                    val response = BackendAPI.retrofitService.login(
                        ApiService.LoginRequest(
                            identifier = state.value.username,
                            password = state.value.password
                        )
                    )

                    if(response.isSuccessful) {
                        val user = User(
                            id = response.body()?.id ?: 0L,
                            accessToken = response.body()?.accessToken ?: "",
                            refreshToken = response.body()?.refreshToken ?: "",
                            name = response.body()?.name ?: "",
                        )

                        _navigationEvent.value = Event(LoginAction.OnLoginSuccess(user))
                    }else
                        _navigationEvent.value = Event(LoginAction.OnLoginError("Login failed"))

                } catch (e: SocketTimeoutException) {
                    Toast.makeText(context, "Connection not available right now", Toast.LENGTH_SHORT).show()
                } catch (e: java.net.UnknownHostException) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                   Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                } catch (e: HttpException) {
                    Log.e("LoginViewModel", "Server error", e)
                    _state.value = _state.value.copy(error = "Invalid username or password")
                } catch (e: Exception) {
                    Log.e("LoginViewModel", "Unknown error", e)
                    _navigationEvent.value = Event(LoginAction.OnLoginError("Something went wrong"))
                }
            }
        }
    }

    private val _launchGoogleSignIn = MutableLiveData<Event<Unit>>()
    val launchGoogleSignIn: LiveData<Event<Unit>> get() = _launchGoogleSignIn

    fun onGoogleSignInClicked() {
        _launchGoogleSignIn.value = Event(Unit)
    }

    suspend fun handleGoogleSignInResult(data: Intent?, context: Context) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val response = BackendAPI.retrofitService.loginGoogle(
                ApiService.LoginGoogleRequest(
                    token = account.idToken
                )
            )

            //obtain the number of the response 500, 400, 200, etc.
            Log.i("LoginViewModel", "Google Sign-In response: $response")


            if(response.isSuccessful) {
                val userGoogle = User(
                    id = response.body()?.id ?: 0L,
                    accessToken = response.body()?.accessToken ?: "",
                    refreshToken = response.body()?.refreshToken ?: "",
                    name = response.body()?.name ?: "",
                )

                Log.i("LoginViewModel", "Google Login successful: Token: ${account.idToken}")

                _navigationEvent.value = Event(LoginAction.OnLoginSuccess(userGoogle))
            }else
                _navigationEvent.value = Event(LoginAction.OnLoginError("Google Sign-In failed"))


        // Handle success
        } catch (e: SocketTimeoutException) {
            Toast.makeText(context, "Connection not available right now", Toast.LENGTH_SHORT).show()
        } catch (e: java.net.UnknownHostException) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
        } catch (e: ApiException) {
            Log.w("LoginViewModel", "Google sign in failed", e)
            _navigationEvent.value = Event(LoginAction.OnLoginError("Google Sign-In failed"))
        }finally {
            Log.i("LoginViewModel", "Google Sign-In successful")
        }
    }

    private fun checkDataValidity(): Boolean {
        val currentState = state.value
        var isValid = true

        // Reset all error flags first (optional but recommended)
        _state.value = currentState.copy(
            errorUsername = false,
            errorPassword = false,
            errorForm = null
        )

        if (currentState.username.isBlank()) {
            _state.value = _state.value.copy(errorUsername = true)
            isValid = false
        }

        if (currentState.password.isBlank()) {
            _state.value = _state.value.copy(errorPassword = true)
            isValid = false
        }

        if (!isValid) {
            _state.value = _state.value.copy(errorForm = R.string.form_error_required_fields)
        }

        return isValid
    }

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnLoginClicked -> {
                _navigationEvent.value = Event(LoginAction.OnLoginClicked)
            }
            LoginAction.OnBackPressed -> {
                _navigationEvent.value = Event(LoginAction.OnBackPressed)
            }
            is LoginAction.OnLoginGoogleClicked -> {
                onGoogleSignInClicked()
            }
            is LoginAction.OnUsernameChange -> {
                _state.value = _state.value.copy(username = action.username)
            }
            is LoginAction.OnPasswordChange -> {
                _state.value = _state.value.copy(password = action.password)
            }
            is LoginAction.OnRegisterClicked -> {
                _navigationEvent.value = Event(LoginAction.OnRegisterClicked)
            }
            is LoginAction.OnLoginSuccess -> {
                _navigationEvent.value = Event(LoginAction.OnLoginSuccess(action.user))
            }
            is LoginAction.OnLoginError -> {
                _navigationEvent.value = Event(LoginAction.OnLoginError(action.error))
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


