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
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.data.backendConection.ApiService

class PreRegisterViewModel : ViewModel() {

    private val _state = MutableStateFlow(PreRegisterScreenState())
    val state = _state

    private val _navigationEvent = MutableLiveData<Event<PreRegisterAction>>()
    val navigationEvent: LiveData<Event<PreRegisterAction>> = _navigationEvent

    private val _launchGoogleSignIn = MutableLiveData<Event<Unit>>()
    val launchGoogleSignIn: LiveData<Event<Unit>> get() = _launchGoogleSignIn


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
            Log.i("PreRegisterViewModel", "Google Sign-In response: $response")


            if(response.isSuccessful) {
                val userGoogle = User(
                    id = response.body()?.id ?: 0L,
                    accessToken = response.body()?.accessToken ?: "",
                    refreshToken = response.body()?.refreshToken ?: "",
                    name = response.body()?.name ?: "",
                )

                Log.i("PreRegisterViewModel", "Google Login successful: Token: ${account.idToken}")

                _navigationEvent.value = Event(PreRegisterAction.OnGoogleLoginSuccess(userGoogle))
            }else
                _navigationEvent.value = Event(PreRegisterAction.OnGoogleLoginError("Google Sign-In failed"))


            // Handle success
        } catch (e: SocketTimeoutException) {
            Toast.makeText(context, "Connection not available right now", Toast.LENGTH_SHORT).show()
        } catch (e: java.net.UnknownHostException) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        } catch (e: ApiException) {
            Log.e("PreRegisterViewModel", "Google sign in failed", e)
            _navigationEvent.value = Event(PreRegisterAction.OnGoogleLoginError("Google Sign-In failed"))
        } catch (e: Exception) {
            Log.e("PreRegisterViewModel", "Unknown error during Google Sign-In", e)
            Toast.makeText(context, "Something went wrong: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            Log.i("PreRegisterViewModel", "Google Sign-In attempt finished")
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
            is PreRegisterAction.OnLoginGoogleClicked -> {
                onGoogleSignInClicked()
            }
            is PreRegisterAction.OnGoogleLoginSuccess -> {
                _navigationEvent.value = Event(PreRegisterAction.OnGoogleLoginSuccess(action.user))
            }
            is PreRegisterAction.OnGoogleLoginError -> {
                _navigationEvent.value = Event(PreRegisterAction.OnGoogleLoginError(action.error))
            }
        }
    }

}

