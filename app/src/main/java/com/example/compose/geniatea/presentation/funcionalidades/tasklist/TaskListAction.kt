package com.example.compose.geniatea.presentation.funcionalidades.tasklist

interface TaskListAction {
    data object OnBackPressed : TaskListAction
    data class OnDeleteNodePressed(val position: Int) : TaskListAction
    data class OnCheckNodePressed(val path: List<Int>, val checked: Boolean) : TaskListAction
    data class OnDateChanged(val date: String) : TaskListAction
    data class OnTimeChanged(val time: String) : TaskListAction
    data class OnNoteChanged(val note: String) : TaskListAction
    data class OnTitleChanged(val title: String) : TaskListAction
    data class OnGeneratingTasksChanged(val isGenerating: Boolean) : TaskListAction
}