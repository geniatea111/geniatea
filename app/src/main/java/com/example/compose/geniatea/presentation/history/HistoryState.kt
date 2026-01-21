package com.example.compose.geniatea.presentation.history

import com.example.compose.geniatea.data.backendConection.ApiService

data class HistoryState(
    val sessions: List<ApiService.ChatSessionResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
