package com.example.compose.geniatea.presentation.funcionalidades.judge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

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
                // Lógica para juzgar la consulta
                val judgmentText = if (_state.value.consulta.contains("bueno", ignoreCase = true)) {
                    "La consulta es positiva."
                } else {
                    "La consulta es negativa."
                }
                _state.value = _state.value.copy(judgment = judgmentText)
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