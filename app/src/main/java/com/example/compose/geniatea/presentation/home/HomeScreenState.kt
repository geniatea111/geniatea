package com.example.compose.geniatea.presentation.home

import com.example.compose.geniatea.data.Conversation

data class HomeScreenState(
    val saludo: String = "",
    val conversations: List<Conversation> = emptyList(),
    val pendingTasks: List<String> = emptyList()
)
