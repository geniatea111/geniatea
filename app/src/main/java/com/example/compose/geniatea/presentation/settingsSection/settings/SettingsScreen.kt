package com.example.compose.geniatea.presentation.settingsSection.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.outlinedButtonColors
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.CustomDialog
import com.example.compose.geniatea.presentation.components.CustomDialogLanguage
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.presentation.home.HomeAction
import com.example.compose.geniatea.presentation.settingsSection.preferencesSettings.PreferencesAction
import com.example.compose.geniatea.presentation.userManagementSection.preregister.PreRegisterAction
import com.example.compose.geniatea.theme.DtGetaiFont
import com.example.compose.geniatea.theme.DtGetaiTypography
import com.example.compose.geniatea.theme.GenIATEATheme


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun SettingsRoot(
    viewModel: SettingsViewModel,
    onBackPressed: () -> Unit,
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
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
    state : SettingsScreenState
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

        var showDialog by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 20.dp)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

               Box(
                  modifier = Modifier
                      .clip(RoundedCornerShape(20.dp))
                      .background(MaterialTheme.colorScheme.primaryContainer)
                      .paint(
                          painter = painterResource(id = R.drawable.deco2),
                          contentScale = ContentScale.Crop,
                            alignment = Alignment.CenterEnd

                      )
                      .fillMaxWidth()
               ){

                   Row(
                       modifier = Modifier
                           .padding(20.dp)
                           .fillMaxWidth(),
                       verticalAlignment = Alignment.CenterVertically
                   ){
                       Column(
                           modifier = Modifier.weight(1f).padding(top = 5.dp)
                       ){
                           Text(
                               text = "Laura",
                               style =  DtGetaiTypography.titleLarge,
                               fontSize = 17.sp

                           )

                           Text(
                                 text = "Laura05"
                           )
                       }

                       Button(
                            onClick = { onAction(SettingsAction.OnAccountPressed) },
                            colors = outlinedButtonColors(
                                 containerColor = MaterialTheme.colorScheme.surface
                            )
                       ) {
                           Icon (
                               painter = painterResource(id = R.drawable.svg_edit_account),
                               contentDescription = null,
                               tint = MaterialTheme.colorScheme.onSurface,
                           )
                       }
                   }
               }

                Button(
                    onClick = { showDialog = showDialog.not() },
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .height(50.dp)
                        .fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon (
                        painter = painterResource(id = R.drawable.svg_logout),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 10.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.logout),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = W700
                    )
                }

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

                generalSection(onAction = onAction)
                personalizationSection(onAction = onAction, state)
                appSection(onAction = onAction)
            }
        }
    }
}


@Composable
fun generalSection(onAction: (SettingsAction) -> Unit = {}) {
    var showDialogLanguage by remember { mutableStateOf(false) }

    Text(
        text = stringResource(id = R.string.general),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
        color = MaterialTheme.colorScheme.onSurface
    )

    ButtonConfig(
        icon = R.drawable.svg_sparkles,
        text = stringResource(id = R.string.ia_settings),
        onClick = { onAction(SettingsAction.OnAISettingsPressed) },
        shape = RoundedCornerShape(topEndPercent = 30, topStartPercent = 30, bottomEndPercent = 0, bottomStartPercent = 0)
    )
    ButtonConfig(
        icon = R.drawable.svg_bell,
        text = stringResource(id = R.string.notifications),
        onClick = { onAction(SettingsAction.OnNotificationPress) },
        offsetValue = (-1),
        shape = RoundedCornerShape(0)
    )
    ButtonConfig(
        icon = R.drawable.svg_language,
        text = stringResource(id = R.string.language),
        onClick = { showDialogLanguage = true },
        offsetValue = (-2),
        shape = RoundedCornerShape(topEndPercent = 0, topStartPercent = 0, bottomEndPercent = 30, bottomStartPercent = 30)
    )

    when {
        showDialogLanguage -> {
            CustomDialogLanguage(
                onDismissRequest = { showDialogLanguage = false },
                onConfirmation = {
                    showDialogLanguage = false
                },
                onAction = onAction
            )
        }
    }
}

@Composable
fun personalizationSection(onAction: (SettingsAction) -> Unit = {}, state: SettingsScreenState) {
    Text(
        text = stringResource(id = R.string.personalization),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp).fillMaxWidth(),
        color = MaterialTheme.colorScheme.onSurface
    )

    ButtonConfig(
        icon = R.drawable.svg_pictograms,
        text = stringResource(id = R.string.show_pictograms),
        onClick = { onAction(SettingsAction.OnPictogramsToggle(state.isPictosEnabled)) },
        shape = RoundedCornerShape(topEndPercent = 30, topStartPercent = 30, bottomEndPercent = 0, bottomStartPercent = 0),
        isSwitchButton = true,
        checked = state.isPictosEnabled
    )

    ButtonConfig(
        icon = R.drawable.svg_moon,
        text = stringResource(id = R.string.dark_mode),
        onClick = { onAction(SettingsAction.OnDarkModeToggle(state.isDarkMode)) },
        offsetValue = (-1),
        shape = RoundedCornerShape(0),
        isSwitchButton = true,
        checked = state.isDarkMode
    )


    ButtonConfig(
        icon = R.drawable.svg_animation,
        text = stringResource(id = R.string.animations),
        onClick = { onAction(SettingsAction.OnAnimationsToggle(state.isAnimationsEnabled)) },
        offsetValue = (-2),
        shape = RoundedCornerShape(0),
        isSwitchButton = true,
        checked = state.isAnimationsEnabled
    )

    ButtonConfig(
        icon = R.drawable.svg_logo,
        text = stringResource(id = R.string.icon_app),
        onClick = { onAction(SettingsAction.OnAppIconPressed) },
        offsetValue = (-3),
        shape = RoundedCornerShape(0)
    )

    ButtonConfig(
        icon = R.drawable.svg_palette,
        text = stringResource(id = R.string.color_app),
        onClick = { onAction(SettingsAction.OnAppColorPressed) },
        offsetValue = (-4),
        shape = RoundedCornerShape(topEndPercent = 0, topStartPercent = 0, bottomEndPercent = 30, bottomStartPercent = 30)
    )
}

@Composable
fun appSection(onAction: (SettingsAction) -> Unit = {}){

    Text(
        text = stringResource(id = R.string.application),
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

@Composable
fun ButtonConfig(
    icon: Int,
    text: String,
    onClick: () -> Unit,
    offsetValue: Int = 0,
    shape: RoundedCornerShape,
    isSwitchButton : Boolean = false,
    checked : Boolean = false,
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
        if (isSwitchButton) {
            Switch(
                checked = checked,
                onCheckedChange = { isChecked ->
                    onClick()
                },
            )
        }else {
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
fun SettingsPreview() {
    GenIATEATheme {
        SettingsScreen(
            onAction = {},
            state = SettingsScreenState()
        )
    }
}

@Preview
@Composable
fun SettingsDarkPreview() {
    GenIATEATheme(isDarkTheme = true) {
        SettingsScreen(
            onAction = {},
            state = SettingsScreenState()
        )
    }
}