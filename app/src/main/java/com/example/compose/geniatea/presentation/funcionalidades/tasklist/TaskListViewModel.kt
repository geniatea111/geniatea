package com.example.compose.geniatea.presentation.funcionalidades.tasklist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.example.compose.geniatea.data.backendConection.ApiService
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {

    var state by mutableStateOf(TaskListScreenState())
        private set

    var stateBottomSheet by mutableStateOf(BottomsheetState())
        private set

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val response = BackendAPI.retrofitService.getTasks()
                if (response.isSuccessful) {
                    val tasks = response.body()?.map { it.toTaskNode() } ?: emptyList()
                    state = state.copy(tasks = tasks, isLoading = false)
                } else {
                    state = state.copy(error = "Error: ${response.code()}", isLoading = false)
                }
            } catch (e: Exception) {
                state = state.copy(error = e.message, isLoading = false)
            }
        }
    }

    private fun createTask() {
        viewModelScope.launch {
            try {
                val response = BackendAPI.retrofitService.createTask(
                    task = ApiService.CreateTaskRequest(
                        title = stateBottomSheet.taskTitle,
                        time = stateBottomSheet.taskTime,
                        date = stateBottomSheet.taskDate,
                        note = stateBottomSheet.taskNote,
                        parentId = null,
                        subtasks = stateBottomSheet.tasks.map { it.toCreateTaskRequest() }
                    )
                )
                if (response.isSuccessful) {
                    loadTasks()
                    state = state.copy(isBottomSheetVisible = false)
                } else {
                    state = state.copy(error = "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                state = state.copy(error = e.message)
            }
        }
    }

    private fun TaskNode.toCreateTaskRequest(): ApiService.CreateTaskRequest {
        return ApiService.CreateTaskRequest(
            title = this.title,
            time = this.time,
            date = "", // Add date to TaskNode or handle it differently
            note = "", // Add note to TaskNode or handle it differently
            parentId = null,
            subtasks = this.subtasks.map { it.toCreateTaskRequest() }
        )
    }

    private fun TaskDTO.toTaskNode(): TaskNode {
        return TaskNode(
            id = this.id,
            title = this.title,
            time = this.time,
            isCompleted = this.completed,
            subtasks = this.subtasks.map { it.toTaskNode() }
        )
    }

    fun deleteNode(position: Int) {
        state = state.copy(
            tasks = state.tasks.toMutableList().apply {
                if (position in indices) {
                    removeAt(position)
                }
            }
        )
    }

    private val _actionEvent = MutableLiveData<Event<TaskListAction>>()
    val navigationEvent: LiveData<Event<TaskListAction>> = _actionEvent

    fun onAction(action: TaskListAction) {
        when (action) {
            TaskListAction.OnBackPressed -> {
                _actionEvent.value = Event(TaskListAction.OnBackPressed)
            }

            TaskListAction.OnShowBottomSheet -> {
                state = state.copy(isBottomSheetVisible = true)
            }

            TaskListAction.OnHideBottomSheet -> {
                state = state.copy(isBottomSheetVisible = false)
            }

            TaskListAction.OnCreateTaskClicked -> {
                createTask()
            }

            is TaskListAction.OnDeleteNodePressed -> {
                deleteNode(action.position)
            }

            is TaskListAction.OnDateChanged -> {
                stateBottomSheet = stateBottomSheet.copy(taskDate = action.date)
            }

            is TaskListAction.OnTimeChanged -> {
                stateBottomSheet = stateBottomSheet.copy(taskTime = action.time)
            }

            is TaskListAction.OnNoteChanged -> {
                stateBottomSheet = stateBottomSheet.copy(taskNote = action.note)
            }

            is TaskListAction.OnTitleChanged -> {
                stateBottomSheet = stateBottomSheet.copy(taskTitle = action.title)
            }

            is TaskListAction.OnNewSubtaskChanged -> {
                stateBottomSheet = stateBottomSheet.copy(newSubtask = action.newSubtask)
            }

            is TaskListAction.OnAddTask -> {
                val newSubtask = TaskNode(title = action.title, time = "") // You might want to get time from somewhere
                stateBottomSheet = stateBottomSheet.copy(
                    tasks = stateBottomSheet.tasks + newSubtask,
                    newSubtask = ""
                )
            }

            is TaskListAction.OnCheckNodePressed -> {
                val task = findTaskNode(state.tasks, action.path)
                task?.let {
                    updateTaskStatus(it.id, action.checked)
                }
                state = state.copy(
                    tasks = checkAt(state.tasks, action.path, action.checked)
                )
            }

            is TaskListAction.OnGeneratingTasksChanged -> {
                stateBottomSheet = stateBottomSheet.copy(
                    isGeneratingTasks = action.isGenerating
                )
            }

            is TaskListAction.OnGenerateSubtask -> {
                generateSubtask(action.taskTitle, action.parentIndex)
            }

            is TaskListAction.OnDeleteSubtask -> {
                val updatedTasks = stateBottomSheet.tasks.toMutableList()
                if (action.index in updatedTasks.indices) {
                    updatedTasks.removeAt(action.index)
                    stateBottomSheet = stateBottomSheet.copy(tasks = updatedTasks)
                }
            }
        }
    }

    fun checkAt(nodes: List<TaskNode>, indices: List<Int>, checked: Boolean): List<TaskNode> {
        if (indices.isEmpty()) return nodes
        val head = indices.first()
        val tail = indices.drop(1)

        return nodes.toMutableList().apply {
            this[head] = if (tail.isEmpty()) {
                this[head].copy(
                    isCompleted = checked,
                    subtasks = setAllSubtasks(this[head].subtasks, checked)
                )
            } else {
                this[head].copy(
                    subtasks = checkAt(this[head].subtasks, tail, checked)
                )
            }
        }
    }

    fun setAllSubtasks(nodes: List<TaskNode>, checked: Boolean): List<TaskNode> =
        nodes.map { node ->
            node.copy(
                isCompleted = checked,
                subtasks = setAllSubtasks(node.subtasks, checked)
            )
        }

    private fun findTaskNode(nodes: List<TaskNode>, indices: List<Int>): TaskNode? {
        if (indices.isEmpty()) return null
        val index = indices.first()
        if (index < 0 || index >= nodes.size) return null
        val node = nodes[index]
        if (indices.size == 1) return node
        return findTaskNode(node.subtasks, indices.drop(1))
    }

    private fun updateTaskStatus(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                BackendAPI.retrofitService.updateTask(
                    taskId,
                    ApiService.UpdateTaskRequest(isCompleted)
                )
            } catch (e: Exception) {
                // Handle error
                state = state.copy(error = e.message)
            }
        }
    }

    private fun generateSubtask(taskTitle: String, parentIndex: Int? = null) {
        viewModelScope.launch {
            stateBottomSheet = stateBottomSheet.copy(isGeneratingTasks = true)
            try {
                val response = BackendAPI.retrofitService.generateSubtask(
                    ApiService.GenerateSubtaskRequest(taskTitle)
                )
                if (response.isSuccessful) {
                    val newSubtaskTitle = response.body()?.string() ?: ""
                    if (newSubtaskTitle.isNotEmpty()) {
                        val newSubtask = TaskNode(title = newSubtaskTitle, time = "")

                        if (parentIndex != null) {
                             // Add as subtask of the task at parentIndex
                             val updatedTasks = stateBottomSheet.tasks.toMutableList()
                             if (parentIndex in updatedTasks.indices) {
                                 val parent = updatedTasks[parentIndex]
                                 updatedTasks[parentIndex] = parent.copy(
                                     subtasks = parent.subtasks + newSubtask
                                 )
                                 stateBottomSheet = stateBottomSheet.copy(tasks = updatedTasks)
                             }
                        } else {
                            // Add to root list
                            stateBottomSheet = stateBottomSheet.copy(
                                tasks = stateBottomSheet.tasks + newSubtask
                            )
                        }
                    }
                } else {
                     // Handle error
                     state = state.copy(error = "Error generating subtask: ${response.code()}")
                }
            } catch (e: Exception) {
               state = state.copy(error = e.message)
            } finally {
               stateBottomSheet = stateBottomSheet.copy(isGeneratingTasks = false)
            }
        }
    }
}

open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }
}
