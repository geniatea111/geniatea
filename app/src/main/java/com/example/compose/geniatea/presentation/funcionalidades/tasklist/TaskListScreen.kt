package com.example.compose.geniatea.presentation.funcionalidades.tasklist

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.font.FontWeight.Companion.W800
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.BottomSheetTask
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.theme.DMSansFont
import com.example.compose.geniatea.theme.GenIATEATheme
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
        onAction = { action ->
            when (action) {
                is TaskListAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
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

    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TitleAppBar(
                title = "",
                onNavIconPressed = { onNavIconPressed() },
            )
        }
    ) { innerPadding ->
        if (showBottomSheet) {
            BottomSheetTask(
                onAction = onAction,
                state = stateBototmSheet,
                onDismiss = { showBottomSheet = false },
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

                    /*Button(
                        onClick = {},
                        modifier = Modifier
                            .size(50.dp)
                            .padding(5.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        ),
                        contentPadding = PaddingValues(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.svg_history),
                            contentDescription = stringResource(id = R.string.navigation_drawer_open),
                            modifier = Modifier
                        )
                    }*/
                }

                if (state.tasks.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
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

                } else {
                    emptyState(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }


                Button(
                    onClick = { showBottomSheet = true },
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
    onChecked: (List<Int>, Boolean) -> Unit,
) {
    var isOpen by rememberSaveable(node.id) { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        if (isOpen) 180f else 0f, label = "accordion-arrow"
    )

    val visibleTrash by animateFloatAsState(
        if (isOpen || node.subtasks.isEmpty()) 1f else 0f, label = "visibleTrash"
    )

    val backgroundColor = when (depth) {
        0 -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val padding = when (depth) {
        0 -> 10.dp
        1 -> 5.dp
        else -> 0.dp
    }
    
    val innerPadding = when (depth) {
        1 -> 8.dp
        else -> 0.dp
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
            .clip(RoundedCornerShape(35.dp))
            .background(backgroundColor)
            .padding(
                start = (depth * 10).dp,
            )
            .padding(padding)
            .height(if (!isOpen && depth == 0) 48.dp else Dp.Unspecified) // TODO: ANIMAR ESTO
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ✨ Animate between RadioButton and Lottie animation
            AnimatedContent(
                targetState = node.isCompleted,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                },
                label = "radio-to-lottie"
            ) { completed ->
                if (!completed) {
                    RadioButton(
                        selected = false,
                        onClick = { onChecked(path, true) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                } else {
                    // ✅ Lottie animation for completion
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_confetti))
                    val progress by animateLottieCompositionAsState(
                        composition = composition,
                        iterations = 1,
                        restartOnPlay = false
                    )

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier
                            .size(55.dp)
                            .clickable { onChecked(path, false) } // toggle back if clicked
                    )
                }
            }

            Text(
                text = "${node.title} (${node.time})",
                style = if (node.isCompleted){
                    TextStyle(
                        textDecoration = TextDecoration.LineThrough,
                        fontFamily = DMSansFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        letterSpacing = 0.25.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }else{
                    MaterialTheme.typography.bodyMedium
                },
                fontWeight = if (depth == 0) W800 else W400
            )

            Spacer(modifier = Modifier.weight(1f))

            if (depth == 0) {
                if (isOpen || node.subtasks.isEmpty()) {
                    IconButton(
                        onClick = { onDelete(path[0]) },
                        enabled = visibleTrash > 0f,
                        colors = IconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                        ),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.svg_trash),
                            contentDescription = stringResource(id = R.string.delete_account),
                            modifier = Modifier.alpha(visibleTrash)
                        )
                    }
                }

                if (node.subtasks.isNotEmpty()) {
                    IconButton(
                        onClick = { isOpen = !isOpen }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.chevron_down),
                            contentDescription = stringResource(id = R.string.delete_account),
                            modifier = Modifier.rotate(arrowRotation),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        node.subtasks.forEachIndexed { subIndex, subtask ->
            TaskNodeItem(
                node = subtask,
                depth = depth + 1,
                path = path + subIndex,
                onDelete = onDelete,
                onChecked = onChecked
            )
        }
    }
}

@Preview
@Composable
fun PreviewTask() {
    GenIATEATheme {
        ResourcesScreen(
            state = TaskListScreenState(
                tasks = listOf(
                    TaskNode(
                        title = "Project A",
                        time = "2h",
                        isOpen = true,
                        isCompleted = true,
                        subtasks = listOf(
                            TaskNode(
                                title = "Design",
                                time = "1h",
                                subtasks = listOf(
                                    TaskNode(title = "Wireframes", time = "30m", isCompleted = true),
                                    TaskNode(title = "UI Review", time = "30m")
                                ),
                            ),
                            TaskNode(title = "Implementation", time = "1h")
                        )
                    ),
                    TaskNode(
                        title = "Project B",
                        time = "3h",
                        subtasks = listOf(
                            TaskNode(title = "Research", time =  "1h"),
                            TaskNode(title = "Development", time =  "2h")
                        )
                    )
                )
            ),
            stateBototmSheet = BottomsheetState(
                taskTitle = "",
                tasks = emptyList(),
                taskDate = "",
                taskTime = "",
                taskNote = ""
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
fun PreviewTaskBlank() {
    GenIATEATheme {
        ResourcesScreen(
            state = TaskListScreenState(
                tasks = listOf()
            ),
            stateBototmSheet = BottomsheetState(
                taskTitle = "",
                tasks = emptyList(),
                taskDate = "",
                taskTime = "",
                taskNote = ""
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
fun PreviewTaskDark() {
    GenIATEATheme(isDarkTheme = true) {
        ResourcesScreen(
            state = TaskListScreenState(
                tasks = listOf(
                    TaskNode(
                        title = "Project A",
                        time = "2h",
                        isOpen = true,
                        isCompleted = true,
                        subtasks = listOf(
                            TaskNode(
                                title = "Design",
                                time = "1h",
                                subtasks = listOf(
                                    TaskNode(title = "Wireframes", time = "30m", isCompleted = true),
                                    TaskNode(title = "UI Review lorem ipsum redon sdfmsdf sdfjasd faslfjsf sdfksdj sdfjsfkjsd fsdfkd ss ", time = "30m")
                                ),
                            ),
                            TaskNode(title = "Implementation", time = "1h")
                        )
                    ),
                    TaskNode(
                        title = "Project B",
                        time = "3h",
                        subtasks = listOf(
                            TaskNode(title = "Research", time =  "1h"),
                            TaskNode(title = "Development", time =  "2h")
                        )
                    )
                )
            ),
            stateBototmSheet = BottomsheetState(
                taskTitle = "",
                tasks = emptyList(),
                taskDate = "",
                taskTime = "",
                taskNote = ""
            ),
            onAction = {}
        )
    }
}
