package com.example.compose.geniatea.presentation.settingsSection.changePass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.theme.GenIATEATheme
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.geniatea.presentation.components.TextField
import com.example.compose.geniatea.data.meChangePass

@Composable
fun ChangePassRoot(
    viewModel: ChangePassViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChangePassScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ChangePassAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePassScreen(
    state: ChangePassScreenState,
    onAction: (ChangePassAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TitleAppBar(
                title = stringResource(id = R.string.change_password),
                onNavIconPressed = { onAction(ChangePassAction.OnBackPressed) },
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .align(Alignment.Center)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp)) // top spacing
                Passwords(state, onAction)
                Spacer(modifier = Modifier.height(16.dp))
                Buttons(onAction)
                Spacer(modifier = Modifier.height(32.dp)) // bottom spacing
            }
        }
    }
}


@Composable
fun Buttons(onAction: (ChangePassAction) -> Unit){
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { onAction(ChangePassAction.OnChangePassClicked) },
            modifier = Modifier
                .heightIn(min = 45.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.change_password),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = W700
            )
        }

    }
}

@Composable
private fun Passwords(userData: ChangePassScreenState, onAction: (ChangePassAction) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            CurrentPassword(userData, onAction)
            Spacer(modifier = Modifier.height(16.dp))
            Password(userData, onAction)
            ConfirmPassword(userData, onAction)

            val stringErrorId = userData.errorMessage
            Text(
                text = stringResource(id = stringErrorId ?: R.string.empty_string),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun CurrentPassword(userData: ChangePassScreenState, onAction: (ChangePassAction) -> Unit = {}) {
    TextField(
        value = userData.currentPassword,
        onValueChange = { onAction(ChangePassAction.OnCurrentPassChange(it)) },
        label = stringResource(id = R.string.current_password),
        isError = userData.errorCurrentPassword,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        isPasswordField = true,
    )
}

@Composable
private fun Password(userData: ChangePassScreenState, onAction: (ChangePassAction) -> Unit = {}) {
    TextField(
        value = userData.newPassword,
        onValueChange = { onAction(ChangePassAction.OnNewPassChange(it)) },
        label = stringResource(id = R.string.new_password),
        isError = userData.errorNewPassword,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        isPasswordField = true,
    )
}

@Composable
private fun ConfirmPassword(userData: ChangePassScreenState, onAction: (ChangePassAction) -> Unit = {}) {
    TextField(
        value = userData.confirmNewPassword,
        onValueChange = { onAction(ChangePassAction.OnConfirmNewPassChange(it)) },
        label = stringResource(id = R.string.confirm_new_password),
        isError = userData.errorConfirmNewPassword,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        isPasswordField = true,
    )
}


@Preview
@Composable
fun ChangePassPreview() {
    GenIATEATheme {
        ChangePassScreen(
            meChangePass,
            onAction = {}
        )
    }
}

@Preview
@Composable
fun ChangePassPreviewEmpty() {
    GenIATEATheme {
        ChangePassScreen(
            ChangePassScreenState(),
            onAction = {}
        )
    }
}

@Preview
@Composable
fun ChangePassPreviewDark() {
    GenIATEATheme(isDarkTheme = true) {
        ChangePassScreen(
            meChangePass,
            onAction = {}
        )
    }
}
