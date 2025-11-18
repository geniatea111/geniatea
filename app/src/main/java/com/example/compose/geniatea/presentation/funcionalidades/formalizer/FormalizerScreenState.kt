package com.example.compose.geniatea.presentation.funcionalidades.formalizer

import androidx.compose.runtime.Immutable

@Immutable
data class FormalizerScreenState(
    val consulta: String = "",
    val resultado: String = "",
    val formality: String = "Formal"
)
