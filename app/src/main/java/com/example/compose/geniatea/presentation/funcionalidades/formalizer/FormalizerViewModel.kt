package com.example.compose.geniatea.presentation.funcionalidades.formalizer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class FormalizerViewModel: ViewModel() {

    val _state = MutableStateFlow(FormalizerScreenState())

    var state = _state
    private val _actionEvent = MutableLiveData<Event<FormalizerAction>>()
    val navigationEvent: LiveData<Event<FormalizerAction>> = _actionEvent

    fun onAction(action: FormalizerAction) {
        when (action) {
            FormalizerAction.OnBackPressed -> {
                _actionEvent.value = Event(FormalizerAction.OnBackPressed)
            }
            is FormalizerAction.OnFormalityChanged -> {
                _state.value = _state.value.copy(formality = action.formality)
            }
            is FormalizerAction.OnConsultaChange -> {
                _state.value = _state.value.copy(consulta = action.consulta)
            }
            is FormalizerAction.OnResultadoChange -> {
                _state.value = _state.value.copy(resultado = action.resultado)
            }
            is FormalizerAction.OnCopyPressed -> {
                _actionEvent.value = Event(FormalizerAction.OnCopyPressed(action.text))
            }
            FormalizerAction.OnConvertPressed -> {
                // Lógica para convertir el texto según la formalidad seleccionada
                val convertedText = when (_state.value.formality) {
                    "Formal" -> "Este es un texto formal convertido."
                    "Informal" -> "Este es un texto informal convertido."
                    else -> _state.value.consulta
                }
                _state.value = _state.value.copy(resultado = convertedText)
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