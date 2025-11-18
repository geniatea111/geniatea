package com.example.compose.geniatea.presentation.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.settingsSection.preferencesSettings.PreferencesAction
import com.example.compose.geniatea.presentation.settingsSection.settings.SettingsAction
import java.util.Locale


@Composable
fun CustomDialogLanguage(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onAction: (SettingsAction) -> Unit = {},
) {
    Dialog(
        onDismissRequest = {
            onDismissRequest()
        }) {
        Card(shape = RoundedCornerShape(30.dp)){
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(20.dp)) {
                Button(
                    onClick = { onDismissRequest() },
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopEnd),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    ),
                    contentPadding = PaddingValues(5.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.svg_close),
                        contentDescription = stringResource(id = R.string.navigation_drawer_close),
                        modifier = Modifier
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.language),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 25.dp, bottom =  25.dp, start = 16.dp, end = 16.dp)
                    )

                    ListLanguages(onAction)

                    Button(
                        onClick = { onConfirmation() },
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    ) {
                        Row{
                            Text(
                                text = stringResource(id = R.string.okay),
                                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
                                fontWeight = W700,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun ListLanguages(
    onAction: (SettingsAction) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        val spanish = stringResource(id = R.string.spanish)
        val english = stringResource(id = R.string.english)
        val french = stringResource(id = R.string.french)

        //first letter uppercase
        val currentLanguage = Locale.getDefault().displayLanguage.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
        Log.i("LanguageTag", "Current language: $currentLanguage")

        listItemLanguage(spanish, icon = painterResource(id = R.drawable.svg_spain), spanish == currentLanguage, onAction)
        listItemLanguage(english, icon = painterResource(id = R.drawable.svg_england), english == currentLanguage, onAction)
        listItemLanguage(french, icon = painterResource(id = R.drawable.svg_france), french == currentLanguage, onAction)
    }
}


@Composable
fun listItemLanguage(text: String, icon: Painter, isChoosen: Boolean = false, onAction: (SettingsAction) -> Unit = {},){

    val backgroundColor = if (isChoosen) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.fillMaxWidth(),
            onClick = { onAction(SettingsAction.OnLanguagePress(text)) },
        ) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(24.dp)

            )

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f).padding(5.dp)
            )
        }
    }
}

@Preview
@Composable
fun DialogFullLanguagePreview() {
    CustomDialogLanguage(
        onDismissRequest = {},
        onConfirmation = {},
    )
}