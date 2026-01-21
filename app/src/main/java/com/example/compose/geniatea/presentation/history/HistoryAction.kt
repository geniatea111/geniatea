package com.example.compose.geniatea.presentation.history

sealed interface HistoryAction {
    data object OnBackPressed : HistoryAction
    data class OnSessionClicked(val sessionId: Long) : HistoryAction
}
