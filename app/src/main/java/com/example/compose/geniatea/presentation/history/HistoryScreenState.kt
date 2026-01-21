package com.example.compose.geniatea.presentation.history

import com.example.compose.geniatea.data.backendConection.ApiService

data class HistoryScreenState(
    val sessions: List<ApiService.ChatSessionResponse> = emptyList()
)
