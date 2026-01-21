package com.example.compose.geniatea.presentation.home

import com.example.compose.geniatea.data.backendConection.ApiService


data class HomeScreenState(
    val saludo: String = "",
    val isLoading: Boolean = false,
    val error: String = "",
    val success: Boolean = false,
    val showDialog: Boolean = false,
    val showBottomSheet: Boolean = false,
    val showSnackBar: Boolean = false,
    val snackBarMessage: String = "",
    val showToast: Boolean = false,
    val toastMessage: String = "",
    val recentConversations: List<ApiService.ChatSessionResponse> = emptyList()
)
