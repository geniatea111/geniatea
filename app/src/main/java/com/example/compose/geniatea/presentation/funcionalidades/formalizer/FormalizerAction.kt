package com.example.compose.geniatea.presentation.funcionalidades.formalizer

interface FormalizerAction {
    data object OnBackPressed : FormalizerAction
    data class OnFormalityChanged(val formality: String) : FormalizerAction
    data class OnConsultaChange(val consulta: String) : FormalizerAction
    data class OnResultadoChange(val resultado: String) : FormalizerAction
    data class OnCopyPressed(val text: String) : FormalizerAction
    data object OnConvertPressed : FormalizerAction
}