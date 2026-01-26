package com.example.compose.geniatea.presentation.funcionalidades.formalizer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.backendConection.ApiService
import com.example.compose.geniatea.data.backendConection.BackendAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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
                viewModelScope.launch {
                    try {
                        val response = BackendAPI.retrofitService.rewrite(
                            ApiService.RewriteRequest(
                                text = _state.value.consulta,
                                style = _state.value.formality
                            )
                        )
                        if (response.isSuccessful) {
                            val convertedText = response.body()?.string() ?: ""
                             _state.value = _state.value.copy(resultado = convertedText)
                        } else {
                             _state.value = _state.value.copy(resultado = "Error: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(resultado = "Error: ${e.message}")
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