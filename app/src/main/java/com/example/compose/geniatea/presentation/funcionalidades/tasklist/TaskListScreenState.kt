package com.example.compose.geniatea.presentation.funcionalidades.tasklist

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class TaskListScreenState(
    val tasks: List<TaskNode> = emptyList(),

)

data class BottomsheetState(
    var taskTitle: String = "",
    val tasks: List<TaskNode> = emptyList(),
    val taskDate: String = "",
    var taskTime: String = "",
    var taskNote: String = "",
    var isGeneratingTasks : Boolean = false,
)

@Immutable
data class TaskNode(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val time: String,
    var isOpen: Boolean = false,
    var isCompleted: Boolean = false,
    val subtasks: List<TaskNode> = emptyList()
)
