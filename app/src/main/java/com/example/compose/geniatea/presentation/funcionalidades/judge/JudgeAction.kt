package com.example.compose.geniatea.presentation.funcionalidades.judge

interface JudgeAction {
    data object OnBackPressed : JudgeAction
    data class OnConsultaChange(val consulta: String) : JudgeAction
    data class OnResultadoChange(val resultado: String) : JudgeAction
    data object OnJudgePressed : JudgeAction
}