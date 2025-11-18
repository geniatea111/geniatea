package com.example.compose.geniatea.presentation.funcionalidades.judge

import androidx.compose.runtime.Immutable

@Immutable
data class JudgeScreenState(
    val consulta: String,
    val judgment: String = "",
)
