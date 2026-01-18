package com.example.compose.geniatea.presentation.settingsSection.accountSettings

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.compose.geniatea.R
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.data.backendConection.ApiService
import com.example.compose.geniatea.data.backendConection.ApiService.GetUserResponse
import com.example.compose.geniatea.data.StoreDataUser
import com.example.compose.geniatea.domain.User
import com.example.compose.geniatea.utils.Event
import com.example.compose.geniatea.utils.Formats.Companion.formatDate
import com.example.compose.geniatea.utils.Formats.Companion.formatDateToBack
import com.example.compose.geniatea.utils.Formats.Companion.formatGender
import com.example.compose.geniatea.utils.Formats.Companion.formatGenderToBack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first


class AccountViewModel: ViewModel() {

    private val _state = MutableStateFlow(AccountState())
    val state = _state

    private val _actionEvent = MutableLiveData<Event<AccountAction>>()
    val actionEvent: LiveData<Event<AccountAction>> = _actionEvent

    fun onAction(action: AccountAction) {
        when (action) {
            AccountAction.OnBackPressed -> {
                _actionEvent.value = Event(AccountAction.OnBackPressed)
            }
            AccountAction.OnPasswordUpdatePressed -> {
                _actionEvent.value = Event(AccountAction.OnPasswordUpdatePressed)
            }
            AccountAction.OnUpdatePressed -> {
                _actionEvent.value = Event(AccountAction.OnUpdatePressed)
            }
            is AccountAction.OnNameChange -> {
                _state.value = _state.value.copy(name = action.name)
            }
            is AccountAction.OnBirthDateChange -> {
                _state.value = _state.value.copy(birthDate = action.birthDate)
            }
            is AccountAction.OnGenderChange -> {
                _state.value = _state.value.copy(gender = action.gender)
            }
            is AccountAction.OnDeleteAccountPressed -> {
                _actionEvent.value = Event(AccountAction.OnDeleteAccountPressed)
            }
        }
    }

    suspend fun setData(context: Context) {
        val store = StoreDataUser(context)
        val id = store.getId() ?: return

        try{
            val response = BackendAPI.retrofitService.getUserById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.i("AccountViewModel", "User data retrieved successfully: $it")
                    updateState(it)
                }
                return
            } else {
                val error = response.errorBody()?.string()
                Log.e("AccountViewModel", "Error retrieving user data [${response.code()}]: $error")
            }
        } catch (e: java.net.SocketTimeoutException) {
            Toast.makeText(context, "Connection not available right now", Toast.LENGTH_SHORT).show()
        } catch (e: java.net.UnknownHostException) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateState(user: GetUserResponse) {
        _state.value = _state.value.copy(
            name = user.name,
            email = user.email,
            username = user.username,
            birthDate = formatDate(user.birthdate),
            gender = formatGender(user.gender)
        )
    }

    suspend fun updateData(context: Context) {
        if(checkDataValidity()) {
            val store = StoreDataUser(context)
            val id = store.getId() ?: 0L
            val token = store.getToken() ?: ""
            val refreshToken = store.getRefreshToken() ?: ""
            val user = User(id, token, refreshToken, _state.value.name, _state.value.email, _state.value.username, _state.value.birthDate, _state.value.gender, "USER", false)

            try{
                val response = BackendAPI.retrofitService.updateUser(
                    token = "Bearer $token",
                    userId = id.toString(),
                    updaterRequest = ApiService.UpdateUserRequest(
                        name = _state.value.name,
                        birthdate = formatDateToBack(_state.value.birthDate),
                        gender = formatGenderToBack(_state.value.gender),
                        showPictograms = null
                    )
                )

                if (response.isSuccessful) {
                    val message = response.body()?.string()
                    Log.i("AccountViewModel", "Server said: $message")
                    if (message?.contains("User updated successfully") == true) {
                        store.updateUser(user)
                        _actionEvent.value = Event(AccountAction.OnUpdateSuccess)
                    }else{
                        Log.e("AccountViewModel", "Error updating user: $message")
                        _actionEvent.value = Event(AccountAction.OnUpdateError)
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                Toast.makeText(context, "Connection not available right now", Toast.LENGTH_SHORT).show()
            } catch (e: java.net.UnknownHostException) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkDataValidity(): Boolean{
        val currentState = state.value
        var isValid = true

        _state.value = currentState.copy(
            errorName = false,
            errorGender = false,
            errorBirthDate = false,
            errorForm = null
        )


        if (currentState.name.isBlank()) {
            _state.value = _state.value.copy(errorName = true)
            isValid = false
        }

        if (currentState.birthDate.isBlank()) {
            _state.value = _state.value.copy(errorBirthDate = true)
            isValid = false
        }

        if (currentState.gender.isBlank()) {
            _state.value = _state.value.copy(errorGender = true)
            isValid = false
        }

        if (!isValid) {
            _state.value = _state.value.copy(errorForm = R.string.form_error_required_fields)
        }

        return isValid
    }

    suspend fun deleteAccount(context: Context) {
        val store = StoreDataUser(context)
        val id = store.getId()!!
        val token = store.getToken() ?: ""

        try {
            val response = BackendAPI.retrofitService.deleteUser(
                token = "Bearer $token",
                id = id
            )
            /*TODO: MIRAR ESTO MEJOR*/
            if (response.isSuccessful) {
                try {
                    val bodyString = response.body()?.string()

                    if (bodyString != null && bodyString.contains("User deleted successfully")) {
                        Log.i("AccountViewModel", "User deleted successfully")
                        _actionEvent.value = Event(AccountAction.OnDeleteAccountSuccess)
                        store.logoutUser()
                    } else {
                        Log.e("AccountViewModel", "Unexpected response body (possible token issue or malformed content): $bodyString")
                        _actionEvent.value = Event(AccountAction.OnDeleteAccountError)
                    }

                } catch (e: Exception) { // Catch broader issues, including malformed/misexpected content
                    Log.e("AccountViewModel", "Error reading response: ${e.message}")
                    _actionEvent.value = Event(AccountAction.OnDeleteAccountError)
                }
            } else {
                val error = response.errorBody()?.string()
                Log.e("AccountViewModel", "Error deleting user [${response.code()}]: $error")
                _actionEvent.value = Event(AccountAction.OnDeleteAccountError)
            }
        } catch (e: Exception) {
            Log.e("AccountViewModel", "Request failed: ${e.message}")
            _actionEvent.value = Event(AccountAction.OnDeleteAccountError)
        }
    }

}
