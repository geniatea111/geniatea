package com.example.compose.geniatea.presentation.components

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

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    title: String = "New Update Available",
    subtitle: String = "This is an example of the description of a very beautiful dialog which you may like.",
    buttontxt: String = "Update Tonight",
    iconButton: Painter = painterResource(R.drawable.svg_logout),
    onConfirmation: () -> Unit,
) {
    Dialog(
        onDismissRequest = {
        onDismissRequest()
    }) {
        Card(shape = RoundedCornerShape(30.dp)){
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(8.dp)) {
                Button(
                    onClick = { onDismissRequest() },
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.TopEnd)
                        .padding(5.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    ),
                    contentPadding = PaddingValues(6.dp)
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
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 25.dp, bottom =  15.dp, start = 16.dp, end = 16.dp)
                    )

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 16.dp),
                    )
                    Button(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    ) {
                        Row{
                            Icon(
                                painter = iconButton,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(18.dp)
                                    .align(Alignment.CenterVertically)
                            )
                            Text(
                                text = buttontxt,
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
@Preview
@Composable
fun DialogFullPreview() {
    CustomDialog(
        onDismissRequest = {},
        onConfirmation = {},
    )
}