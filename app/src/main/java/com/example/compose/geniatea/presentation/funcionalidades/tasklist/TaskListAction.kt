package com.example.compose.geniatea.presentation.funcionalidades.tasklist

interface TaskListAction {
    data object OnBackPressed : TaskListAction
    data object OnCreateTaskClicked : TaskListAction
    data object OnShowBottomSheet : TaskListAction
    data object OnHideBottomSheet : TaskListAction
    data class OnDeleteNodePressed(val position: Int) : TaskListAction
    data class OnCheckNodePressed(val path: List<Int>, val checked: Boolean) : TaskListAction
    data class OnDateChanged(val date: String) : TaskListAction
    data class OnTimeChanged(val time: String) : TaskListAction
    data class OnNoteChanged(val note: String) : TaskListAction
    data class OnTitleChanged(val title: String) : TaskListAction
    data class OnNewSubtaskChanged(val newSubtask: String) : TaskListAction
    data class OnAddTask(val title: String) : TaskListAction
    data class OnGeneratingTasksChanged(val isGenerating: Boolean) : TaskListAction
    data class OnGenerateSubtask(val taskTitle: String, val parentIndex: Int? = null) : TaskListAction
    data class OnDeleteSubtask(val index: Int) : TaskListAction
}