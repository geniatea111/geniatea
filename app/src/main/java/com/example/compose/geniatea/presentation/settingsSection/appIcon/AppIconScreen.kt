package com.example.compose.geniatea.presentation.settingsSection.appIcon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.theme.GenIATEATheme
import com.example.compose.geniatea.presentation.components.WorkProgress

@Composable
fun AppIconRoot(
    viewModel: AppIconViewModel,
    onBackPressed: () -> Unit,
) {
    AppIconScreen(
        onAction = { action ->
            when (action) {
                is AppIconAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppIconScreen(
    onAction: (AppIconAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TitleAppBar(
                title = stringResource(id = R.string.icon_app),
                onNavIconPressed = { onAction(AppIconAction.OnBackPressed) }
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 20.dp)
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WorkProgress()
            }
        }
    }
}


@Preview
@Composable
fun RegisterPreview() {
    GenIATEATheme {
        AppIconScreen(
            onAction = {}
        )
    }
}