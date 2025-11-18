package com.example.compose.geniatea.presentation.settingsSection.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.ButtonDefaults.outlinedButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.CustomDialog
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.presentation.home.HomeAction
import com.example.compose.geniatea.theme.GenIATEATheme


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun SettingsRoot(
    viewModel: SettingsViewModel,
    onBackPressed: () -> Unit,
) {

    SettingsScreen(
        onAction = { action ->
            when (action) {
                is HomeAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onAction: (SettingsAction) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TitleAppBar(
                title =  stringResource(id = R.string.settings),
                onNavIconPressed = { onAction(SettingsAction.OnBackPressed) }
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
        Image(
            painter = painterResource(id = R.drawable.deco1),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight(0.20f),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.placeholder),
                    contentDescription = stringResource(id = R.string.app_name),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .height(80.dp)
                )

                Text(
                    text = stringResource(id = R.string.account),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 30.dp, bottom = 8.dp).fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface
                )
                ButtonConfig(
                    icon = R.drawable.svg_user_round,
                    text = stringResource(id = R.string.account_data),
                    onClick = { onAction(SettingsAction.OnAccountPressed) },
                    shape = RoundedCornerShape(topEndPercent = 30, topStartPercent = 30, bottomEndPercent = 0, bottomStartPercent = 0)
                )

               /* ButtonConfig(
                    icon = R.drawable.svg_moon,
                    text = stringResource(id = R.string.dark_mode),
                    onClick = { },
                    isDarkButton = true,
                    state = state,
                    onAction = onAction,
                    offsetValue = (-1),
                    shape = RoundedCornerShape(0),
                )*/
                ButtonConfig(
                    icon = R.drawable.svg_logout,
                    text = stringResource(id = R.string.logout),
                    onClick = { showDialog = showDialog.not() },
                    offsetValue = (-1),
                    shape = RoundedCornerShape(topEndPercent = 0, topStartPercent = 0, bottomEndPercent = 30, bottomStartPercent = 30)
                )

                when {
                    showDialog -> {
                        CustomDialog(
                            onDismissRequest = { showDialog = false },
                            title = stringResource(id = R.string.title_dialog_logout),
                            subtitle = stringResource(id = R.string.subtitle_dialog_logout),
                            buttontxt = stringResource(id = R.string.logout),
                            onConfirmation = {
                                onAction(SettingsAction.OnLogoutPressed)
                                showDialog = false
                            }
                        )
                    }
                }

//                    AlertDialog(
//                        containerColor = MaterialTheme.colorScheme.background,
//                        icon = {
//                            Icon(
//                                imageVector = Icons.Rounded.Logout,
//                                contentDescription = null,
//                                tint = MaterialTheme.colorScheme.onSurface
//                            )
//                        },
//                        onDismissRequest = { showDialog = false },
//                        title = { Text(stringResource(id = R.string.title_dialog_logout),
//                            style = dialogTitle,
//                        ) },
//                        confirmButton = {
//                            Button(onClick = {
//                                onAction(SettingsAction.OnLogoutPressed)
//                                showDialog = false
//                            }) {
//                                Text(stringResource(id = R.string.logout))
//                            }
//                        },
//                        dismissButton = {
//                            OutlinedButton(onClick = { showDialog = false }) {
//                                Text(stringResource(id = R.string.cancel))
//                            }
//                        },
//                    )

             //   }

                Text(
                    text = stringResource(id = R.string.general),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp).fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface
                )
                ButtonConfig(
                    icon = R.drawable.svg_preferences,
                    text = stringResource(id = R.string.preferences),
                    onClick = { onAction(SettingsAction.OnPreferencesPressed) },
                    shape = RoundedCornerShape(topEndPercent = 30, topStartPercent = 30, bottomEndPercent = 0, bottomStartPercent = 0)
                )
                ButtonConfig(
                    icon = R.drawable.svg_sparkles,
                    text = stringResource(id = R.string.ia_settings),
                    onClick = { onAction(SettingsAction.OnAISettingsPressed) },
                    offsetValue = (-1),
                    shape = RoundedCornerShape(topEndPercent = 0, topStartPercent = 0, bottomEndPercent = 30, bottomStartPercent = 30)
                )

                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp).fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface
                )
                ButtonConfig(
                    icon = R.drawable.svg_info,
                    text = stringResource(id = R.string.about),
                    onClick = { onAction(SettingsAction.OnAboutPressed) },
                    shape = RoundedCornerShape(topEndPercent = 30, topStartPercent = 30, bottomEndPercent = 0, bottomStartPercent = 0)
                )
                ButtonConfig(
                    icon = R.drawable.svg_privacy,
                    text = stringResource(id = R.string.privacy_policy),
                    onClick = { onAction(SettingsAction.OnPrivacyPolicyPressed) },
                    offsetValue = (-1),
                    shape = RoundedCornerShape(topEndPercent = 0, topStartPercent = 0, bottomEndPercent = 30, bottomStartPercent = 30)
                )
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainerLow),
        colors = outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
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

            Icon(
                imageVector = Icons.Rounded.ArrowForwardIos,
                tint = MaterialTheme.colorScheme.outline,
                contentDescription = null,
            )
    }

}

@Preview
@Composable
fun SettingsPreview() {
    GenIATEATheme {
        SettingsScreen(
            onAction = {}
        )
    }
}

@Preview
@Composable
fun SettingsDarkPreview() {
    GenIATEATheme(isDarkTheme = true) {
        SettingsScreen(
            onAction = {}
        )
    }
}