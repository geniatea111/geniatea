package com.example.compose.geniatea.presentation.funcionalidades.judge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.backendConection.ApiService
import com.example.compose.geniatea.data.backendConection.BackendAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class JudgeViewModel: ViewModel() {

    val _state = MutableStateFlow(JudgeScreenState(
        consulta = "",
        judgment = "",
    ))
    
    var state = _state
    private val _actionEvent = MutableLiveData<Event<JudgeAction>>()
    val navigationEvent: LiveData<Event<JudgeAction>> = _actionEvent

    fun onAction(action: JudgeAction) {
        when (action) {
            JudgeAction.OnBackPressed -> {
                _actionEvent.value = Event(JudgeAction.OnBackPressed)
            }

            is JudgeAction.OnResultadoChange -> {
                _state.value = _state.value.copy(judgment = action.resultado)
            }

            is JudgeAction.OnConsultaChange -> {
                _state.value = _state.value.copy(consulta = action.consulta)
            }

            JudgeAction.OnJudgePressed -> {
                viewModelScope.launch {
                    try {
                        val response = BackendAPI.retrofitService.analyzeIntent(
                            ApiService.AnalyzeIntentRequest(_state.value.consulta)
                        )
                        if (response.isSuccessful) {
                            val judgmentText = response.body()?.string() ?: ""
                            _state.value = _state.value.copy(judgment = judgmentText)
                        } else {
                             _state.value = _state.value.copy(judgment = "Error: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(judgment = "Error: ${e.message}")
                    }
                }
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