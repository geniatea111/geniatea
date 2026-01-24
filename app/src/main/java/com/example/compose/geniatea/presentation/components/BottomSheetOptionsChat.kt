package com.example.compose.geniatea.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.geniatea.presentation.funcionalidades.chat.ChatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetOptions(
    onDismiss: () -> Unit = {},
    chatStyle: ChatStyle,
    onStyleChange: (ChatStyle) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = Modifier,
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {

        Column(
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text(
                text = "Estilo de respuesta",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp, start = 15.dp, end = 15.dp).fillMaxWidth()
            )

            Option(
                title = "Normal",
                description = "Respuestas estándar",
                isChoosen = chatStyle == ChatStyle.NORMAL,
                onClick = { onStyleChange(ChatStyle.NORMAL) }
            )
            Option(
                title = "Conciso",
                description = "Respuestas más breves y directas",
                isChoosen = chatStyle == ChatStyle.CONCISE,
                onClick = { onStyleChange(ChatStyle.CONCISE) }
            )

            Option(
                title = "Aprendizaje",
                description = "Respuestas que fomentan el aprendizaje y la comprensión",
                isChoosen = chatStyle == ChatStyle.LEARNING,
                onClick = { onStyleChange(ChatStyle.LEARNING) }
            )

        }
    }
}

@Composable
fun Option(
    title: String,
    description: String,
    isChoosen: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isChoosen) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.padding(vertical = 5.dp, horizontal = 15.dp).fillMaxWidth(),
        onClick = onClick,

        ) {
        Column() {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}


@Preview
@Composable
fun BottomSheetOptionsPreview() {
    BottomSheetOptions(chatStyle = ChatStyle.NORMAL, onStyleChange = {})
}
