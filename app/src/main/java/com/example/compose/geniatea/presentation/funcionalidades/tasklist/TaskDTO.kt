package com.example.compose.geniatea.presentation.funcionalidades.tasklist

import com.google.gson.annotations.SerializedName

data class TaskDTO(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("time") val time: String,
    @SerializedName("completed") val completed: Boolean,
    @SerializedName("subtasks") val subtasks: List<TaskDTO> = emptyList()
)
