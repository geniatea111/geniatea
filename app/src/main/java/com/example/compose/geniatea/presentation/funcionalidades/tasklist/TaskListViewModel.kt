package com.example.compose.geniatea.presentation.funcionalidades.tasklist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class TaskListViewModel: ViewModel() {

    val input = """
        *Project A | 2h
        **Design | 1h
        ***Wireframes | 30m
        ***UI Review | 30m
        **Implementation | 1h
        *Project B | 3h
        **Research | 1h
        **Development | 2h
        *Project C | 2h
        **Planning | 1h
        ***Meeting | 1h
        ***Notes | 30m
        **Execution | 1h
        *Project D | 1h
        """.trimIndent()

    val input2 = """
        *Encuentra el gimnasio que deseas visitar | 2h
        *Create Slides | 1h
        *Design Layout | 30m
        *Add Content | 30m
        *Practice Delivery | 1h
        *Team Meeting | 1h
        """.trimIndent()
    

    var state by mutableStateOf(TaskListScreenState(
        tasks = parseTasksFromString(input),
    ))
        private set


    var stateBottomSheet by mutableStateOf(BottomsheetState(
        taskTitle = "",
        tasks = parseTasksFromString(input2),
        taskDate = "",
        taskTime = "",
        taskNote = ""
    ))
        private set


    fun deleteNode(position: Int) {
        state = state.copy(
            tasks = state.tasks.toMutableList().apply {
                if (position in indices) {
                    removeAt(position)
                }
            }
        )
       // state.tasks[position].isOpen = false
    }


    private val _actionEvent = MutableLiveData<Event<TaskListAction>>()
    val navigationEvent: LiveData<Event<TaskListAction>> = _actionEvent

    fun onAction(action: TaskListAction) {
        when (action) {
            TaskListAction.OnBackPressed -> {
                _actionEvent.value = Event(TaskListAction.OnBackPressed)
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
            is TaskListAction.OnCheckNodePressed -> {
                state = state.copy(
                    tasks = checkAt(state.tasks, action.path, action.checked)
                )
            }
            is TaskListAction.OnGeneratingTasksChanged -> {
                stateBottomSheet = stateBottomSheet.copy(
                    isGeneratingTasks = action.isGenerating
                )
            }
        }
    }

    fun checkAt(nodes: List<TaskNode>, indices: List<Int>, checked: Boolean): List<TaskNode> {
        if (indices.isEmpty()) return nodes
        val head = indices.first()
        val tail = indices.drop(1)

        return nodes.toMutableList().apply {
            this[head] = if (tail.isEmpty()) {
                // If we are at the target node, update it and ALL its subtasks
                this[head].copy(
                    isCompleted = checked,
                    subtasks = setAllSubtasks(this[head].subtasks, checked)
                )
            } else {
                // Otherwise, recurse deeper
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

    fun parseTasksFromString(input: String): List<TaskNode> {
        val lines = input.lines().map { it.trim() }.filter { it.isNotEmpty() }

        val rootTasks = mutableListOf<TaskNode>()
        val stack = ArrayDeque<MutableList<TaskNode>>()
        stack.add(rootTasks) // level 0

        for (line in lines) {
            val level = line.takeWhile { it == '*' }.length  // count number of *
            val content = line.drop(level).trim()

            // Split "title | time" if you want to support times inline
            val parts = content.split("|").map { it.trim() }
            val title = parts.getOrElse(0) { "" }
            val time = parts.getOrElse(1) { "" }

            val node = TaskNode(title = title, time = time)

            // Ensure the stack has enough levels
            while (stack.size > level) {
                stack.removeLast()
            }

            // Add to parent
            stack.last().add(node)

            // Prepare new children list for this node
            stack.add(node.subtasks.toMutableList())

            // Replace node in parent with updated one containing the mutable list
            val parentList = stack[stack.size - 2]
            parentList[parentList.lastIndex] = node.copy(subtasks = stack.last())
        }

       // rootTasks[0].isCompleted = true
        return rootTasks
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