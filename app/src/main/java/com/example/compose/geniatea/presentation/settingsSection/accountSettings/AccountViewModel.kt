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
import com.example.compose.geniatea.utils.Formats.Companion.formatDate
import com.example.compose.geniatea.utils.Formats.Companion.formatDateToBack
import com.example.compose.geniatea.utils.Formats.Companion.formatGender
import com.example.compose.geniatea.utils.Formats.Companion.formatGenderToBack
import kotlinx.coroutines.flow.MutableStateFlow


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
        val store = StoreDataUser()
        val id = store.getId(context) ?: return

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
            val user = User(
                name = _state.value.name,
                email = _state.value.email,
                username = _state.value.username,
                birthdate = _state.value.birthDate,
                gender = _state.value.gender
            )

            try{
                val response = BackendAPI.retrofitService.updateUser(
                    token = "Bearer ${StoreDataUser().getToken(context)}",
                    userId = StoreDataUser().getId(context).toString(),
                    ApiService.UpdateUserRequest(
                        name = _state.value.name,
                        birthdate = formatDateToBack(_state.value.birthDate),
                        gender = formatGenderToBack(_state.value.gender)
                    )
                )

                if (response.isSuccessful) {
                    val message = response.body()?.string()
                    Log.i("AccountViewModel", "Server said: $message")
                    if (message?.contains("User updated successfully") == true) {
                        StoreDataUser().updateUser(context, user)
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
        val id = StoreDataUser().getId(context)!!
        val token = StoreDataUser().getToken(context) ?: ""

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
                        StoreDataUser().logoutUser(context)
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