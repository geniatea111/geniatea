package com.example.compose.geniatea.presentation.funcionalidades.resourcestea

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.compose.geniatea.presentation.components.WorkProgress
import com.example.compose.geniatea.theme.GenIATEATheme
import com.example.compose.geniatea.theme.titleApp

@Composable
fun ResourcesRoot(
    viewModel: ResourcesViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ResourcesScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ResourcesAction.OnBackPressed -> onBackPressed()
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
    state: ResourcesScreenState,
    onAction: (ResourcesAction) -> Unit,
    onNavIconPressed: () -> Unit = { },
    modifier: Modifier = Modifier,
    ) {
    Scaffold(
        topBar = {
            TitleAppBar(
                title = stringResource(id = R.string.home),
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            val saludo = state.content

            Text(
                text = saludo,
                style = titleApp,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(Alignment.TopStart)
            )

            WorkProgress()
        }
    }
}


@Preview
@Composable
fun PreviewResources() {
    GenIATEATheme {
        ResourcesScreen(
            state = ResourcesScreenState(
                content = "Hola, Usuario",
            ),
            onAction = {}
        )
    }
}