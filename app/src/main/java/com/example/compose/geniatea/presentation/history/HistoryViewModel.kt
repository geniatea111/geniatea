package com.example.compose.geniatea.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.data.StoreDataUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class HistoryViewModel(private val dataStore: StoreDataUser) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state = _state.asStateFlow()

    init {
        getChatSessions()
    }

    fun getChatSessions() {
        viewModelScope.launch {
            _state.value = HistoryState(isLoading = true)
            try {
                val token = dataStore.getToken()
                if (token != null) {
                    val response = BackendAPI.retrofitService.getChatSessions("Bearer $token")
                    if (response.isSuccessful) {
                        _state.value = HistoryState(sessions = response.body() ?: emptyList())
                    } else {
                        _state.value = HistoryState(error = "Error: ${response.message()}")
                    }
                }
            } catch (e: HttpException) {
                _state.value = HistoryState(error = "Error: ${e.message()}")
            } catch (e: IOException) {
                _state.value = HistoryState(error = "Error de red. Por favor, comprueba tu conexión.")
            }
        }
    }
}