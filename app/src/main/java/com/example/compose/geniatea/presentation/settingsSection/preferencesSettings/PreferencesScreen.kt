package com.example.compose.geniatea.presentation.settingsSection.preferencesSettings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.theme.GenIATEATheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.geniatea.presentation.components.CustomDialogLanguage

@Composable
fun PreferencesRoot(
    viewModel: PreferencesViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AboutScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is PreferencesAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onAction: (PreferencesAction) -> Unit,
    state : PreferencesScreenState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TitleAppBar(
                title = stringResource(id = R.string.preferences),
                onNavIconPressed = { onAction(PreferencesAction.OnBackPressed) }
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
            var showDialog by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ButtonConfig(
                    icon = R.drawable.svg_moon,
                    text = stringResource(id = R.string.dark_mode),
                    onClick = {},
                    isDarkButton = true,
                    state = state,
                    onAction = onAction,
                    shape = RoundedCornerShape(topEndPercent = 30, topStartPercent = 30, bottomEndPercent = 0, bottomStartPercent = 0)
                )
                ButtonConfig(
                    icon = R.drawable.svg_bell,
                    text = stringResource(id = R.string.notifications),
                    onClick = { onAction(PreferencesAction.OnNotificationPress) },
                    isDarkButton = false,
                    state = state,
                    onAction = onAction,
                    offsetValue = (-1),
                    shape = RoundedCornerShape(0)
                )
                ButtonConfig(
                    icon = R.drawable.svg_language,
                    text = stringResource(id = R.string.language),
                    onClick = { showDialog = true },
                    isDarkButton = false,
                    state = state,
                    onAction = onAction,
                    offsetValue = (-2),
                    shape = RoundedCornerShape(topEndPercent = 0, topStartPercent = 0, bottomEndPercent = 30, bottomStartPercent = 30)
                )

                when {
                    showDialog -> {
                        CustomDialogLanguage(
                            onDismissRequest = { showDialog = false },
                            onConfirmation = {
                                showDialog = false
                            },
                            //onAction = onAction
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ButtonConfig(
    icon: Int,
    text: String,
    onClick: () -> Unit,
    isDarkButton: Boolean,
    state: PreferencesScreenState,
    onAction: (PreferencesAction) -> Unit,
    offsetValue: Int = 0,
    shape: RoundedCornerShape
) {

    OutlinedButton(
        onClick = { onClick() },
        modifier = Modifier
            .height(55.dp)
            .fillMaxWidth()
            .offset(y = offsetValue.dp),
        shape = shape,
        contentPadding = PaddingValues(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Icon(
            painter =  painterResource(id = icon),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
            modifier = Modifier.padding(end = 10.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(
            modifier = Modifier.weight(1f)
        )
        if (isDarkButton) {
            Switch(
                checked = state.isDarkMode,
                onCheckedChange = { isChecked ->
                    onAction(PreferencesAction.OnDarkModeToggle(isChecked))
                },
            )
        }else{
            Icon(
                imageVector = Icons.Rounded.ArrowForwardIos,
                tint = MaterialTheme.colorScheme.outline,
                contentDescription = null,
            )
        }
    }

}


@Preview
@Composable
fun RegisterPreview() {
    GenIATEATheme {
        AboutScreen(
            onAction = {},
            state = PreferencesScreenState()
        )
    }
}