package com.example.compose.geniatea.presentation.funcionalidades.judge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.theme.GenIATEATheme

@Composable
fun JudgeRoot(
    viewModel: JudgeViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    JudgeScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is JudgeAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        onNavIconPressed = { onBackPressed() }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JudgeScreen(
    state: JudgeScreenState,
    onAction: (JudgeAction) -> Unit,
    onNavIconPressed: () -> Unit = { },
    modifier: Modifier = Modifier,
    ) {
    Scaffold(
        topBar = {
            TitleAppBar(
                title = stringResource(id = R.string.judge),
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


                Box(modifier = modifier.fillMaxWidth()){
                    OutlinedTextField(
                        value = state.consulta,
                        onValueChange = { onAction(JudgeAction.OnConsultaChange(it)) },
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
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
                        shape = RoundedCornerShape(30.dp))

                    Button(
                        onClick = { onAction(JudgeAction.OnJudgePressed) },
                        modifier = Modifier
                            .padding(23.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Text(
                            text = stringResource(id = R.string.juzgar),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }


                OutlinedTextField(
                    value = state.judgment,
                    onValueChange = { onAction(JudgeAction.OnResultadoChange(it)) },
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
                    readOnly = true,


                )


            }

        }
    }
}


@Preview
@Composable
fun PreviewJudge() {
    GenIATEATheme {
        JudgeScreen(
            state = JudgeScreenState(
                consulta = "Este es un texto de ejemplo.",
                judgment = "Este es el juicio del texto de ejemplo."
            ),
            onAction = {}
        )
    }
}