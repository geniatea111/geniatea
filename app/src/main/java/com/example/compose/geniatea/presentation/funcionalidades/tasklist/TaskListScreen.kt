package com.example.compose.geniatea.presentation.funcionalidades.tasklist

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.font.FontWeight.Companion.W800
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.BottomSheetTask
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.theme.DMSansFont
import com.example.compose.geniatea.theme.subtitleApp
import com.example.compose.geniatea.theme.titleApp

@Composable
fun ResourcesRoot(
    viewModel: TaskListViewModel,
    onBackPressed: () -> Unit,
) {

    val state = viewModel.state
    val stateBototmSheet = viewModel.stateBottomSheet

    ResourcesScreen(
        state = state,
        stateBototmSheet = stateBototmSheet,
        onAction = {
            viewModel.onAction(it)
        },
        onNavIconPressed = { onBackPressed() }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(
    state: TaskListScreenState,
    stateBototmSheet: BottomsheetState,
    onAction: (TaskListAction) -> Unit,
    onNavIconPressed: () -> Unit = { },
) {

    Scaffold(
        topBar = {
            TitleAppBar(
                title = "",
                onNavIconPressed = { onNavIconPressed() },
            )
        }
    ) { innerPadding ->
        if (state.isBottomSheetVisible) {
            BottomSheetTask(
                onAction = onAction,
                state = stateBototmSheet,
                onDismiss = { onAction(TaskListAction.OnHideBottomSheet) },
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {

            Image(
                painter = painterResource(id = R.drawable.deco1),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight(0.20f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(top = 30.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Mis tareas",
                        style = subtitleApp,
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }

                Box(modifier = Modifier.weight(1f)) {
                    when {
                        state.isLoading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                        state.error != null -> {
                            Text(
                                text = state.error,
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        state.tasks.isEmpty() -> {
                            emptyState(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 10.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                TaskList(
                                    tasks = state.tasks,
                                    onChecked = { path, isCompleted ->
                                        onAction(TaskListAction.OnCheckNodePressed(path, isCompleted))
                                    },
                                    onDelete = { position ->
                                        onAction(TaskListAction.OnDeleteNodePressed(position))
                                    }
                                )

                                TaskListCompletadas(
                                    tasks = state.tasks,
                                    onChecked = { path, isCompleted ->
                                        onAction(TaskListAction.OnCheckNodePressed(path, isCompleted))
                                    },
                                    onDelete = { position ->
                                        onAction(TaskListAction.OnDeleteNodePressed(position))
                                    }
                                )
                            }
                        }
                    }
                }


                Button(
                    onClick = { onAction(TaskListAction.OnShowBottomSheet) },
                    modifier = Modifier
                        .align(Alignment.End)
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Nueva tarea",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = W700
                    )
                }

            }
        }
    }
}

@Composable
fun emptyState(modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.placeholder_task),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Tu lista está vacía",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline
        )

        Text(
            text = "Crea tu primera tarea y empieza a organizarte",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(20.dp),
            textAlign = TextAlign.Center,
            fontWeight = W400,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun TaskList(
    tasks: List<TaskNode>,
    onDelete: (position: Int) -> Unit,
    onChecked: (path: List<Int>, Boolean) -> Unit,
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        tasks.forEachIndexed { index, task ->
            if(!task.isCompleted){
                TaskNodeItem(
                    node = task,
                    depth = 0,
                    path = listOf(index),
                    onDelete = onDelete,
                    onChecked = onChecked
                )
            }

        }
    }
}
@Composable
fun TaskListCompletadas(
    tasks: List<TaskNode>,
    onDelete: (position: Int) -> Unit,
    onChecked: (path: List<Int>, Boolean) -> Unit,
) {
    val completedTasks = tasks.mapIndexedNotNull { index, task ->
        if (task.isCompleted || task.subtasks.any { it.isCompleted }) index to task else null
    }

    if (completedTasks.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
        ) {
            Text(
                text = "Completadas",
                style = titleApp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                completedTasks.forEach { (originalIndex, task) ->
                    TaskNodeItem(
                        node = task,
                        depth = 0,
                        path = listOf(originalIndex),
                        onDelete = onDelete,
                        onChecked = onChecked
                    )
                }
            }
        }
    }
}


@Composable
fun TaskNodeItem(
    node: TaskNode,
    depth: Int,
    path: List<Int>,
    onDelete: (Int) -> Unit,
    onChecked: (path: List<Int>, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(node.isOpen) }
    val alpha = if (node.isCompleted) 0.5f else 1f
    val textDecoration = if (node.isCompleted) TextDecoration.LineThrough else null
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) -180f else 0f,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(35.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(
                top = 20.dp,
                bottom = 20.dp,
                start = (20.dp + (depth * 15).dp),
                end = 20.dp
            )
            .alpha(alpha)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = node.isCompleted,
                onClick = { onChecked(path, !node.isCompleted) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = node.title,
                style = TextStyle(
                    textDecoration = textDecoration,
                    fontFamily = DMSansFont,
                    fontWeight = W800,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (node.subtasks.isNotEmpty()) {
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        modifier = Modifier.rotate(rotationAngle),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        if (isExpanded && node.subtasks.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                node.subtasks.forEachIndexed { index, subtask ->
                    TaskNodeItem(
                        node = subtask,
                        depth = depth + 1,
                        path = path + index,
                        onDelete = onDelete,
                        onChecked = onChecked
                    )
                }
            }
        }
    }
}
