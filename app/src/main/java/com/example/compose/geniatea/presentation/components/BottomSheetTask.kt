package com.example.compose.geniatea.presentation.components

import android.R.attr.enabled
import android.R.attr.text
import android.app.TimePickerDialog
import android.view.ViewTreeObserver
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.funcionalidades.tasklist.TaskListAction
import com.example.compose.geniatea.theme.subtitleApp
import com.example.compose.geniatea.presentation.funcionalidades.tasklist.BottomsheetState
import com.example.compose.geniatea.presentation.funcionalidades.tasklist.TaskNode
import com.example.compose.geniatea.theme.DtGetaiTypography
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetTask(onDismiss : () -> Unit = {}, onAction: (TaskListAction) -> Unit = {}, state: BottomsheetState) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    ModalBottomSheet(
            modifier = Modifier
                .imePadding()
                .padding(top = 30.dp)
                .navigationBarsPadding(),
            onDismissRequest = {
                onDismiss()
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .wrapContentHeight()
                .imePadding() // lifts content above the keyboard
                .verticalScroll(scrollState) // allows scrolling when keyboard is open
                .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.large)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

           title(onAction, state)

            subtareas(onAction, state)
            hora(onAction, state)
            fecha(onAction, state)
            nota(onAction, state)
            
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .height(55.dp),
                shape = CircleShape,
                onClick = {  }
            ) {
                Text(
                    text = stringResource(R.string.create),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = W700
                )
            }
        }
    }
}

@Composable
fun title(onAction: (TaskListAction) -> Unit = {}, state: BottomsheetState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = state.taskTitle,
            onValueChange = {
                if (it.length <= 18)
                    onAction(TaskListAction.OnTitleChanged(it))
            },
            singleLine = true,
            textStyle = DtGetaiTypography.titleMedium.copy(
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .wrapContentHeight(CenterVertically),
            maxLines = 1,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.taskTitle.isEmpty()) {
                        Text(
                            text = "Sin título",
                            style = subtitleApp.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )

    }
}


@Composable
fun subtareas(onAction: (TaskListAction) -> Unit = {}, state: BottomsheetState) {
    var isOpen by remember { mutableStateOf(true) }

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(35.dp),
        onClick = { isOpen = !isOpen },
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 23.dp),

        ){
        Column{

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.svg_list_task),
                    contentDescription = null,
                )

                Text(
                    text = stringResource(R.string.add_subtask),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(CenterVertically)
                        .padding(start = 10.dp)
                )
            }


            if (isOpen) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    state.tasks.forEachIndexed { index, subtask ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 5.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(35.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            tasksList(onAction, state, index, subtask)
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            onAction(TaskListAction.OnGeneratingTasksChanged(true))
                            val updatedTasks = state.tasks.toMutableList()
                            onAction(TaskListAction.OnNoteChanged("")) // You might want to create a new action for adding subtasks
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(52.dp),
                        shape = RoundedCornerShape(35.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(color = MaterialTheme.colorScheme.surface, width = 0.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        AnimatedContent(
                            targetState = state.isGeneratingTasks,
                            label = "GeneratingButtonAnimation",
                            transitionSpec = {
                                // Animate content crossfade + slight slide
                                fadeIn() + slideInVertically { it / 2 } togetherWith
                                        fadeOut() + slideOutVertically { -it / 2 } //cambiar la animación
                            }
                        ) { isGenerating ->
                            Row(
                                verticalAlignment = CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = if (isGenerating)
                                            R.drawable.svg_sparkles_filled
                                        else
                                            R.drawable.svg_sparkles
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .padding(end = 8.dp)
                                        .graphicsLayer {
                                            rotationZ = if (isGenerating) 360f else 0f
                                        },
                                    colorFilter = ColorFilter.tint(Color(0xFF83B7F9))
                                )

                                Text(
                                    text = if (isGenerating) "Generando..." else "Generar con IA",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.W700
                                )
                            }
                        }

                    }

                    OutlinedTextField(
                        value = "",
                        onValueChange = { /* Handle subtask input change */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp)
                            .height(52.dp),
                        placeholder = {
                            Text(
                                text = "Escribe aqui...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                        shape = CircleShape
                                    )
                                    .padding(4.dp)
                                    .clickable {
                                        val updatedTasks = state.tasks.toMutableList()
                                        onAction(TaskListAction.OnNoteChanged("")) // You might want to create a new action for adding subtasks
                                    }
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(30.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f),
                            focusedBorderColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            unfocusedBorderColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }

    }
}


@Composable
fun tasksList(onAction: (TaskListAction) -> Unit = {}, state: BottomsheetState, index: Int, subtask: TaskNode){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 10.dp, end= 5.dp),
        verticalAlignment = CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_circle),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(25.dp)
                .padding(end = 10.dp)
        )
        Text(
            text = subtask.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.align(CenterVertically).weight(0.8f).padding(end = 8.dp),
            maxLines = Int.MAX_VALUE,
            overflow = TextOverflow.Clip
        )
        Icon(
            painter = painterResource(id = R.drawable.svg_sparkles),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(30.dp)
                .padding(4.dp)
                .clickable {
                    val updatedTasks = state.tasks.toMutableList()
                    updatedTasks.removeAt(index)
                    onAction(TaskListAction.OnNoteChanged("")) // You might want to create a new action for removing subtasks
                }
        )

        Icon(
            painter = painterResource(id = R.drawable.svg_trash),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(30.dp)
                .padding(4.dp)
                .clickable {
                    val updatedTasks = state.tasks.toMutableList()
                    updatedTasks.removeAt(index)
                    onAction(TaskListAction.OnNoteChanged("")) // You might want to create a new action for removing subtasks
                }
        )
    }
}

@Composable
fun hora(onAction: (TaskListAction) -> Unit = {}, state: BottomsheetState) {
    val showTimePicker = remember { mutableStateOf(false) }

    if (showTimePicker.value)
        DialDialog(
            onConfirm = { hour, minute ->
                showTimePicker.value = false
                val formattedTime = String.format("%02d:%02d", hour, minute)
                state.taskTime = formattedTime
                onAction(TaskListAction.OnTimeChanged(formattedTime)) },
            onDismiss = { showTimePicker.value = false }
        )

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.fillMaxWidth(),
        onClick = { showTimePicker.value = true },
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 20.dp),

        ){
        Row(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Image(
                painter = painterResource(id = R.drawable.svg_clock),
                contentDescription = null,
            )

            Text (
                text = stringResource(R.string.hora),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(start = 10.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text (
                text = state.taskTime,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(start = 10.dp)
            )
        }
    }
}


@Composable
fun fecha(onAction: (TaskListAction) -> Unit = {}, state: BottomsheetState) {
    var showModal by rememberSaveable { mutableStateOf(false) }

    if (showModal) {
        DatePickerModal(
            onDateSelected = { dateMillis ->
                showModal = false
                if (dateMillis != null) {
                    onAction(TaskListAction.OnDateChanged(convertMillisToDate(dateMillis)))
                }
            },
            onDismiss = { showModal = false }
        )
    }

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.fillMaxWidth(),
        onClick = { showModal = true },
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 20.dp),

        ){
        Row(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Image(
                painter = painterResource(id = R.drawable.svg_calendar),
                contentDescription = null
            )

            Text (
                text = stringResource(R.string.fecha),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(start = 10.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text (
                text = state.taskDate,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(start = 10.dp)
            )
        }
    }
}

@Composable
fun nota(onAction: (TaskListAction) -> Unit = {}, state: BottomsheetState) {

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),

        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(35.dp),
        onClick = {  },
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 20.dp)
    ){

        Column{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.svg_note),
                    contentDescription = null,
                )

                Text (
                    text = stringResource(R.string.nota),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(CenterVertically)
                        .padding(start = 10.dp),

                    )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                BasicTextField(
                    value = state.taskNote,
                    onValueChange = { onAction(TaskListAction.OnNoteChanged(it)) },
                    singleLine = false,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopStart)
                )

                if (state.taskNote.isEmpty()) {
                    Text(
                        text = stringResource(R.string.add_note),
                        modifier = Modifier.align(Alignment.TopStart).fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialDialog(
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            tonalElevation = 6.dp
        ) {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(8.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.select_time),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 12.dp),
                        textAlign = TextAlign.Center
                    )
                    TimePicker(
                        state = timePickerState,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(  onClick = {
                            onConfirm(timePickerState.hour, timePickerState.minute)
                        }) {
                            Text(text = stringResource(R.string.okay))
                        }
                    }
                }

                Button(
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.TopEnd)
                        .padding(5.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    ),
                    contentPadding = PaddingValues(6.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.svg_close),
                        contentDescription = stringResource(id = R.string.navigation_drawer_close),
                        modifier = Modifier
                    )
                }
            }
        }

    }
}


fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}


@Preview
@Composable
fun BottomSheetTaskPreview() {
    subtareas(
        onAction = {},
        state = BottomsheetState(
            taskTitle = "Nueva tarea",
            taskNote = "",
            taskTime = "12:00",
            tasks = emptyList(),
            taskDate = "12/06/2024",
        )
    )
}