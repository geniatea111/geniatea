package com.example.compose.geniatea.presentation.userManagementSection.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.data.meProfile
import com.example.compose.geniatea.theme.GenIATEATheme
import com.example.compose.geniatea.presentation.components.TextField


@Composable
fun LoginRoot(
    viewModel: LoginViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoginScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is LoginAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    state: LoginScreenState,
    onAction: (LoginAction) -> Unit,
) {
   Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TitleAppBar(
                title = stringResource(id = R.string.login),
                onNavIconPressed = { onAction(LoginAction.OnBackPressed) },
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {

//            Image(
//                painter = painterResource(id = R.drawable.deco1),
//                contentDescription = null,
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .fillMaxHeight(0.20f),
//                contentScale = ContentScale.Crop
//            )

            Image(
                painter = painterResource(id = R.drawable.deco2),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                UserInfoFields(state, onAction)
                val stringErrorId = state.errorForm
                Text(
                    text = stringResource(id = stringErrorId ?: R.string.empty_string),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Buttons(onAction)
            }


            TextButton(
                onClick = { onAction(LoginAction.OnRegisterClicked) },
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .align(Alignment.BottomCenter)
                    .wrapContentWidth(),
            ) {
                Row{
                    Text(
                        text = stringResource(id = R.string.no_account1),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = R.string.no_account2),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                }
            }
        }
    }
}

@Composable
fun Buttons(onAction: (LoginAction) -> Unit){
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { onAction(LoginAction.OnLoginClicked) },
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.surface,
            )
        ) {
            Text(
                text = stringResource(id = R.string.login),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = W700
            )
        }

        Image(
            painter = painterResource(id = R.drawable.separator),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 30.dp)
        )

        OutlinedButton(
            onClick = { onAction(LoginAction.OnLoginGoogleClicked) },
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = stringResource(id = R.string.google_login),
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.google_login),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = W700
            )
        }

    }
}

@Composable
private fun UserInfoFields(userData: LoginScreenState, onAction: (LoginAction) -> Unit = {}) {
    Column(modifier = Modifier
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically))
    {
        UsernameEmail(userData, onAction)
        Password(userData, onAction)
    }
}

@Composable
private fun UsernameEmail(userData: LoginScreenState, onAction: (LoginAction) -> Unit = {}) {
    TextField(
        value = userData.username,
        onValueChange = { onAction(LoginAction.OnUsernameChange(it)) },
        label = stringResource(id = R.string.usernameEmail),
        isError = userData.errorUsername
    )
}

@Composable
private fun Password(userData: LoginScreenState, onAction: (LoginAction) -> Unit = {}) {
    TextField(
        value = userData.password,
        onValueChange = { onAction(LoginAction.OnPasswordChange(it)) },
        label = stringResource(id = R.string.password),
        isError = userData.errorPassword,
        isPasswordField = true
    )
}

@Preview
@Composable
fun LoginPreview() {
    GenIATEATheme {
        LoginScreen(
            meProfile,
            onAction = {}
        )
    }
}

@Preview
@Composable
fun LoginPreviewDark() {
    GenIATEATheme(isDarkTheme = true) {
        LoginScreen(
            meProfile,
            onAction = {}
        )
    }
}

@Preview
@Composable
fun ButtonsPreview() {
    GenIATEATheme {
        Buttons(
            onAction = {}
        )
    }
}


