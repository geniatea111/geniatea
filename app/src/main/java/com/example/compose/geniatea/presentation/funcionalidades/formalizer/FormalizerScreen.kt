package com.example.compose.geniatea.presentation.funcionalidades.formalizer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.R
import com.example.compose.geniatea.theme.GenIATEATheme
import com.google.android.material.color.MaterialColors.ALPHA_DISABLED
import com.google.android.material.color.MaterialColors.ALPHA_FULL

@Composable
fun FormalizerRoot(
    viewModel: FormalizerViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    FormalizerScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is FormalizerAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        onNavIconPressed = { onBackPressed() }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormalizerScreen(
    state: FormalizerScreenState,
    onAction: (FormalizerAction) -> Unit,
    onNavIconPressed: () -> Unit = { },
    modifier: Modifier = Modifier,
    ) {
    Scaffold(
        topBar = {
            TitleAppBar(
                title = stringResource(id = R.string.formalizer),
                onNavIconPressed = { onNavIconPressed() },
            )
        },
        // Exclude ime and navigation bar padding so this can be added by the UserInput composable
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime)
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ) {

//                Image(
//                    painter = painterResource(id = R.drawable.placeholderformalizacion),
//                    contentDescription = null,
//                    modifier = Modifier.padding(top = 20.dp).heightIn(200.dp).width(200.dp).align(Alignment.CenterHorizontally)
//                )


                OutlinedTextField(
                    value = state.consulta,
                    onValueChange = { onAction(FormalizerAction.OnConsultaChange(it)) },
                    modifier = modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    placeholder = {
                        Text(
                            text = "Introduce el texto que quieres convertir",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )
                    },

                    singleLine = false,
                    shape = RoundedCornerShape(30.dp),

                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 10.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){

                    LargeDropdownMenu(
                        label = "Formalidad",
                        items = listOf("Más formal", "Más informal", "Más profesional", "Más técnico", "Más accesible", "Más educado", "Menos sarcástico", "Más enfadado", "Más facil de leer", "Más sociable", "Menos emocional", "Lista", "Corregir gramática"),
                        selectedIndex = if (state.formality.isEmpty()) -1 else listOf("Más formal", "Más informal", "Más profesional", "Más técnico", "Más accesible", "Más educado", "Menos sarcástico", "Más enfadado", "Más facil de leer", "Más sociable", "Menos emocional", "Lista", "Corregir gramática").indexOf(state.formality),
                        onItemSelected = { _, item -> onAction(FormalizerAction.OnFormalityChanged(item)) },
                    )

                    Button(
                        onClick = { onAction(FormalizerAction.OnConvertPressed) },
                        modifier = Modifier
                            .padding(start = 5.dp, top = 10.dp, bottom = 2.dp)
                            .heightIn(min = 56.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.convert),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }


                }

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.resultado,
                        onValueChange = { onAction(FormalizerAction.OnResultadoChange(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        placeholder = {
                            Text(
                                text = "Resultado",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        },
                        singleLine = false,
                        shape = RoundedCornerShape(30.dp),
                        readOnly = true
                    )

                    if (!state.resultado.isEmpty()) {

                        IconButton(
                            onClick = { onAction(FormalizerAction.OnCopyPressed(state.resultado)) },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.svg_copy),
                                contentDescription = stringResource(id = R.string.clear),
                                modifier = Modifier.alpha(0.6f)
                            )
                        }
                    }

                }

            }

        }
    }
}

@Composable
fun <T> LargeDropdownMenu(
    modifier: Modifier = Modifier,
    label: String,
    notSetLabel: String? = null,
    items: List<T>,
    selectedIndex: Int = -1,
    onItemSelected: (index: Int, item: T) -> Unit,
    selectedItemToString: (T) -> String = { it.toString() },
    drawItem: @Composable (T, Boolean, Boolean, () -> Unit) -> Unit = { item, selected, itemEnabled, onClick ->
        LargeDropdownMenuItem(
            text = item.toString(),
            selected = selected,
            enabled = itemEnabled,
            onClick = onClick,
        )
    },
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.width(220.dp).height(IntrinsicSize.Min)) {
        OutlinedTextField(
            label = { Text(label) },
            value = items.getOrNull(selectedIndex)?.let { selectedItemToString(it) } ?: "",
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            trailingIcon = {
                val icon  = if (expanded) {
                    Icons.Filled.ArrowDropUp
                } else {
                    Icons.Filled.ArrowDropDown
                }
                Icon(icon, "")
            },
            onValueChange = { },
            readOnly = true,
        )

        // Transparent clickable surface on top of OutlinedTextField
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable(enabled = true) { expanded = true },
            color = Color.Transparent,
        ) {}
    }

    if (expanded) {
        Dialog(
            onDismissRequest = { expanded = false },
        ) {
            MaterialTheme {
                Surface(
                    shape = RoundedCornerShape(25.dp),
                ) {
                    val listState = rememberLazyListState()
                    if (selectedIndex > -1) {
                        LaunchedEffect("ScrollToSelected") {
                            listState.scrollToItem(index = selectedIndex)
                        }
                    }

                    LazyColumn(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), state = listState) {
                        if (notSetLabel != null) {
                            item {
                                LargeDropdownMenuItem(
                                    text = notSetLabel,
                                    selected = false,
                                    enabled = false,
                                    onClick = { },
                                )
                            }
                        }
                        itemsIndexed(items) { index, item ->
                            val selectedItem = index == selectedIndex
                            drawItem(
                                item,
                                selectedItem,
                                true
                            ) {
                                onItemSelected(index, item)
                                expanded = false
                            }

                            if (index < items.lastIndex) {
                                Divider(modifier = Modifier.padding(horizontal = 10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LargeDropdownMenuItem(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_DISABLED)
        selected -> MaterialTheme.colorScheme.primary.copy(alpha = ALPHA_FULL)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_FULL)
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(modifier = Modifier
            .clickable(enabled) { onClick() }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 15.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}


@Preview
@Composable
fun PreviewFormalizer() {
    GenIATEATheme {
        FormalizerScreen(
            state = FormalizerScreenState(),
            onAction = {}
        )
    }
}