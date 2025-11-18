package com.example.compose.geniatea.presentation.userManagementSection.preregister

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.TextField
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.theme.GenIATEATheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextDecoration
import androidx.glance.ColorFilter
import com.example.compose.geniatea.data.meEmail
import com.example.compose.geniatea.data.meProfile
import com.example.compose.geniatea.theme.DtGetaiTypography


@Composable
fun PreRegisterRoot(
    viewModel: PreRegisterViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PreRegisterScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is PreRegisterAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreRegisterScreen(
    state: PreRegisterScreenState,
    onAction: (PreRegisterAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TitleAppBar(
                title = "",
                onNavIconPressed = { onAction(PreRegisterAction.OnBackPressed) },
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

                Image(
                    painter = painterResource(id = R.drawable.ic_geniatea),
                    colorFilter = tint(MaterialTheme.colorScheme.onPrimary),
                    contentDescription = null,
                    modifier = Modifier
                        .height(47.dp)
                        .width(62.dp),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = "Solo necesitas tu correo para empezar",
                    style = DtGetaiTypography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(top = 50.dp, bottom = 25.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )


                UserInfoFields(state, onAction)
                val stringErrorId = state.error
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
                onClick = { onAction(PreRegisterAction.OnContinueClicked) },
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .align(Alignment.BottomCenter)
                    .wrapContentWidth(),
            ) {
                Row{
                    Text(
                        text = stringResource(id = R.string.already_account1),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = R.string.already_account2),
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
fun Buttons(onAction: (PreRegisterAction) -> Unit){
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { onAction(PreRegisterAction.OnContinueClicked) },
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.surface,
            )
        ) {
            Text(
                text = stringResource(id = R.string.continues),
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
            onClick = { onAction(PreRegisterAction.OnLoginGoogleClicked) },
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = stringResource(id = R.string.continue_google),
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.continue_google),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = W700
            )
        }

    }
}

@Composable
private fun UserInfoFields(userData: PreRegisterScreenState, onAction: (PreRegisterAction) -> Unit = {}) {
    Column(modifier = Modifier
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically))
    {
        UsernameEmail(userData, onAction)
    }
}

@Composable
private fun UsernameEmail(userData: PreRegisterScreenState, onAction: (PreRegisterAction) -> Unit = {}) {
    TextField(
        value = userData.email,
        onValueChange = { onAction(PreRegisterAction.OnEmailChange(it)) },
        label = stringResource(id = R.string.email),
        isError = userData.errorEmail
    )
}

@Preview
@Composable
fun PreRegisterPreview() {
    GenIATEATheme {
        PreRegisterScreen(
            meEmail,
            onAction = {}
        )
    }
}

@Preview
@Composable
fun PreRegisterPreviewDark() {
    GenIATEATheme(isDarkTheme = true) {
        PreRegisterScreen(
            meEmail,
            onAction = {}
        )
    }
}

