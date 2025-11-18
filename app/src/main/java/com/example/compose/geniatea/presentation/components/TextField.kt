package com.example.compose.geniatea.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.compose.geniatea.R
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.geniatea.theme.GenIATEATheme

const val limitNum = 50

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    isError: Boolean = false,
    isPasswordField: Boolean = false,
    modifier: Modifier = Modifier,
    enabled : Boolean = true,
    hint: String? = null,
    singleLine: Boolean = true,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.take(limitNum)) },
        modifier = modifier
            .fillMaxWidth(),
        label = {
            Text(
                text = label ?: "",
                style = MaterialTheme.typography.bodyLarge
            )
        },

        placeholder = {
            if (hint != null) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
        },
        visualTransformation = when {
            isPasswordField && !passwordVisible -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        },
        trailingIcon = {
            when {
                isPasswordField -> {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter =  if (passwordVisible) painterResource(R.drawable.ic_eye_open) else painterResource(R.drawable.ic_eye_closed),
                            contentDescription = stringResource(id = R.string.password_visibility_toggle),
                            tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
                isError -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_error),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        isError = isError,
        singleLine = singleLine,
        shape = RoundedCornerShape(30.dp),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            capitalization = if (isPasswordField) KeyboardCapitalization.None else KeyboardCapitalization.Words),
        colors =
            if (enabled == false){
                if (isError)
                    OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.error,
                        disabledBorderColor = MaterialTheme.colorScheme.error,
                        disabledLabelColor = MaterialTheme.colorScheme.error
                    )
                else
                    OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
            }else
                OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),

                    errorTextColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error,
                    errorBorderColor = MaterialTheme.colorScheme.error
                )

    )
}


@Preview
@Composable
fun UsernamePreview() {
    GenIATEATheme {
        TextField(
            value = "",
            label = "Nombre de usuario",
            isError = false,
            isPasswordField = false,
            onValueChange = {},
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background).padding(20.dp),
        )
    }
}

@Preview
@Composable
fun UsernameFilledPreview() {
    GenIATEATheme {
        TextField(
            value = "Laura",
            label = "Nombre de usuario",
            isError = false,
            isPasswordField = false,
            onValueChange = {},
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background).padding(20.dp),
        )
    }
}

@Preview
@Composable
fun UsernamePreviewDark() {
    GenIATEATheme(isDarkTheme = true) {
        TextField(
            value = "",
            label = "Nombre de usuario",
            isError = false,
            isPasswordField = false,
            onValueChange = {},
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background).padding(20.dp),
        )
    }
}

@Preview
@Composable
fun UsernameFilledPreviewDark() {
    GenIATEATheme(isDarkTheme = true) {
        TextField(
            value = "Laura",
            label = "Nombre de usuario",
            isError = false,
            isPasswordField = false,
            onValueChange = {},
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background).padding(20.dp),
        )
    }
}

@Preview
@Composable
fun PasswordPreview() {
    GenIATEATheme {
        TextField(
            value = "",
            label = "Nombre de usuario",
            isError = false,
            isPasswordField = true,
            onValueChange = {},
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background).padding(20.dp),
        )
    }
}

@Preview
@Composable
fun PasswordFilledPreview() {
    GenIATEATheme {
        TextField(
            value = "1234567",
            label = "Nombre de usuario",
            isError = false,
            isPasswordField = true,
            onValueChange = {},
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background).padding(20.dp),
        )
    }
}