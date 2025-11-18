package com.example.compose.geniatea.presentation.settingsSection.accountSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.geniatea.R
import com.example.compose.geniatea.presentation.components.CustomDialog
import com.example.compose.geniatea.presentation.components.TextField
import com.example.compose.geniatea.presentation.components.TitleAppBar
import com.example.compose.geniatea.theme.GenIATEATheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun AccountRoot(
    viewModel: AccountViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AccountSettingsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is AccountAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    state: AccountState,
    onAction: (AccountAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TitleAppBar(
                title = stringResource(id = R.string.account),
                onNavIconPressed = { onAction(AccountAction.OnBackPressed) }
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                  //  .align(Alignment.Center)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)

            ) {

//                Image(
//                    painter = rememberVectorPainter(image = Icons.Rounded.AccountCircle),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(100.dp)
//                        .padding(bottom = 20.dp),
//                )

                Username(state)
                Email(state)
                Name(state, onAction)
                Birthdate(state, onAction)
                Gender(state, onAction)

                val stringErrorId = state.errorForm

                Text(
                    text = stringResource(id = stringErrorId ?: R.string.empty_string),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Buttons(onAction)
        }
    }
}

@Composable
fun Buttons(onAction: (AccountAction) -> Unit){
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        contentAlignment = Alignment.BottomCenter,
    ){
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = { onAction(AccountAction.OnUpdatePressed) },
                    modifier = Modifier
                        .heightIn(min = 45.dp)
                        .weight(0.8f)
                ) {
                    Text(
                        text = stringResource(id = R.string.update),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = W700
                    )
                }

                FilledTonalButton(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .heightIn(min = 45.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.svg_trash),
                        contentDescription = null,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { onAction(AccountAction.OnPasswordUpdatePressed) },
                modifier = Modifier
                    .heightIn(min = 45.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.change_password),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    fontWeight = W700
                )
            }

            when {
                showDialog -> {
                    CustomDialog(
                        onDismissRequest = { showDialog = false },
                        title = stringResource(id = R.string.title_dialog_delete_account),
                        subtitle = stringResource(id = R.string.subtitle_dialog_delete_account),
                        buttontxt = stringResource(id = R.string.delete_account),
                        iconButton = painterResource(R.drawable.svg_trash),
                        onConfirmation = {
                            onAction(AccountAction.OnDeleteAccountPressed)
                            showDialog = false
                        }
                    )

                }
            }


        }
    }

}



@Composable
private fun Name(userData: AccountState, onAction: (AccountAction) -> Unit = {}) {
    TextField(
        value = userData.name,
        onValueChange = { onAction(AccountAction.OnNameChange(it)) },
        label = stringResource(id = R.string.name),
        isError = userData.errorName,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        isPasswordField = false,
    )
}

@Composable
private fun Username(userData: AccountState) {
    TextField(
        value = userData.username,
        onValueChange = {},
        label = stringResource(id = R.string.username),
        isError = userData.errorUsername,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        isPasswordField = false,
        enabled = false
    )
}


@Composable
private fun Email(userData: AccountState) {
    TextField(
        value = userData.email,
        onValueChange = {},
        label = stringResource(id = R.string.email),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        isPasswordField = false,
        enabled = false,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Birthdate(userData: AccountState, onAction: (AccountAction) -> Unit = {}) {
    var showModal by rememberSaveable { mutableStateOf(false) }

    if (showModal) {
        DatePickerModal(
            userData.birthDate,
            onDateSelected = { dateMillis ->
                showModal = false
                if (dateMillis != null) {
                    onAction(AccountAction.OnBirthDateChange(convertMillisToDate(dateMillis)))
                }
            },
            onDismiss = { showModal = false }
        )
    }

    // The clickable read-only TextField
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable { showModal = true }
    ) {
        OutlinedTextField(
            value = userData.birthDate,
            onValueChange = {}, // Read-only
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = stringResource(id = R.string.birthdate),
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            singleLine = true,
            trailingIcon = {
                if (userData.errorBirthDate)
                    Icon(
                        imageVector = Icons.Rounded.Cancel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
            },
            isError = userData.errorBirthDate,
            shape = RoundedCornerShape(50.dp),
            colors =
                if (userData.errorBirthDate)
                    ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.error,
                        disabledBorderColor = MaterialTheme.colorScheme.error,
                        disabledLabelColor = MaterialTheme.colorScheme.error
                    )
                else
                    ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
        )
    }
}

fun parseDateToMillis(dateString: String): Long? {
    return try {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = formatter.parse(dateString)
        date?.time
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    initialDate: String,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = parseDateToMillis(initialDate) ?: System.currentTimeMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSelectTextField(
    selectedValue: String,
    genderError: Boolean,
    options: List<String>,
    onValueChangedEvent: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedValue,
            onValueChange = {},
            label = {   Text(
                text = stringResource(R.string.gender),
                style = MaterialTheme.typography.bodyLarge,
            )},
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = genderError,
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors =  OutlinedTextFieldDefaults.colors(
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

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            shape = RoundedCornerShape(20.dp)
        ) {
            options.forEach { option: String ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(horizontal = 5.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        ) },
                    onClick = {
                        expanded = false
                        onValueChangedEvent(option)

                    }
                )
            }
        }
    }
}


@Composable
private fun Gender(userData: AccountState, onAction: (AccountAction) -> Unit = {}) {
    var option by rememberSaveable { mutableStateOf("") }
    option = userData.gender

    DynamicSelectTextField(
        selectedValue = option,
        genderError = userData.errorGender,
        options = listOf(
            stringResource(R.string.male),
            stringResource(R.string.female),
            stringResource(R.string.xGender)
        ),
        onValueChangedEvent = { selectedOption ->
            when (selectedOption) {
                selectedOption -> {
                    onAction(AccountAction.OnGenderChange(selectedOption))
                    option = selectedOption
                }

                else -> onAction(AccountAction.OnGenderChange("X"))
            }
        }
    )
}



@Preview
@Composable
fun AccountPreview() {
    GenIATEATheme {
        AccountSettingsScreen(
            state = AccountState(),
            onAction = {}
        )
    }
}

@Preview
@Composable
fun AccountPreviewDark() {
    GenIATEATheme(isDarkTheme = true) {
        AccountSettingsScreen(
            state = AccountState(),
            onAction = {}
        )
    }
}

