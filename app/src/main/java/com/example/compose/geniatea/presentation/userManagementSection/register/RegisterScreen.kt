package com.example.compose.geniatea.presentation.userManagementSection.register

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.compose.geniatea.data.meRegister
import com.example.compose.geniatea.theme.GenIATEATheme
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import com.example.compose.geniatea.presentation.components.DatePickerModal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun RegisterRoot(
    viewModel: RegisterViewModel,
    onBackPressed: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RegisterScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is RegisterAction.OnBackPressed -> onBackPressed()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    state: RegisterScreenState,
    onAction: (RegisterAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TitleAppBar(
                title =  stringResource(id = R.string.register),
                onNavIconPressed = { onAction(RegisterAction.OnBackPressed) },
            )
        },
        //contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Box( modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
        ){
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
                    .verticalScroll(rememberScrollState())
                    .align(Alignment.Center)
                    .padding(bottom = 40.dp)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.starempty),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(0.20f),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(75.dp)) // top spacing
                UserInfoFields(state, onAction)

            }

            Button(
                onClick = { onAction(RegisterAction.OnRegisterClicked) },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 30.dp)
                    .align(Alignment.BottomCenter)
                    .heightIn(min = 56.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.register),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = W700
                )
            }
        }
    }


}

@Composable
private fun UserInfoFields(userData: RegisterScreenState, onAction: (RegisterAction) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            //Name(userData, onAction)
            Email(userData, onAction)
            Username(userData, onAction)
//            Birthdate(userData, onAction)
//            Gender(userData, onAction)
            Password(userData, onAction)
            ConfirmPassword(userData, onAction)

            val stringErrorId = userData.errorForm
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
//
//@Composable
//private fun Name(userData: RegisterScreenState, onAction: (RegisterAction) -> Unit = {}) {
//     TextField(
//        value = userData.name,
//        onValueChange = { onAction(RegisterAction.OnNameChange(it)) },
//        label = stringResource(id = R.string.name),
//         isError = userData.errorName,
//         modifier = Modifier
//            .fillMaxWidth()
//            .heightIn(min = 56.dp),
//        isPasswordField = false,
//     )
//}

@Composable
private fun Username(userData: RegisterScreenState, onAction: (RegisterAction) -> Unit = {}) {
    TextField(
        value = userData.username,
        onValueChange = { onAction(RegisterAction.OnUsernameChange(it)) },
        label = stringResource(id = R.string.username),
        isError = userData.errorUsername,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        isPasswordField = false,
    )
}

@Composable
private fun Email(userData: RegisterScreenState, onAction: (RegisterAction) -> Unit = {}) {
    TextField(
        value = userData.email,
        onValueChange = { onAction(RegisterAction.OnEmailChange(it)) },
        label = stringResource(id = R.string.email),
        isError = userData.errorEmail,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        isPasswordField = false,
    )
}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun Birthdate(userData: RegisterScreenState, onAction: (RegisterAction) -> Unit = {}) {
//    var showModal by rememberSaveable { mutableStateOf(false) }
//
//    if (showModal) {
//        DatePickerModal(
//            onDateSelected = { dateMillis ->
//                showModal = false
//                if (dateMillis != null) {
//                    onAction(RegisterAction.OnBirthDateChange(convertMillisToDate(dateMillis)))
//                }
//            },
//            onDismiss = { showModal = false }
//        )
//    }
//
//    // The clickable read-only TextField
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .heightIn(min = 64.dp)
//            .clickable { showModal = true }
//    ) {
//        OutlinedTextField(
//            value = userData.birthDate,
//            onValueChange = {}, // Read-only
//            readOnly = true,
//            enabled = false,
//            modifier = Modifier.fillMaxWidth(),
//            label = {
//                Text(
//                    text = stringResource(id = R.string.birthdate),
//                    style = MaterialTheme.typography.bodyLarge,
//                )
//            },
//            singleLine = true,
//            trailingIcon = {
//                if (userData.errorBirthDate)
//                    Icon(
//                        imageVector = Icons.Rounded.Cancel,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.error
//                    )
//            },
//            isError = userData.errorBirthDate,
//            shape = RoundedCornerShape(50.dp),
//            colors =
//                if (userData.errorBirthDate)
//                    ExposedDropdownMenuDefaults.outlinedTextFieldColors(
//                        disabledTextColor = MaterialTheme.colorScheme.error,
//                        disabledBorderColor = MaterialTheme.colorScheme.error,
//                        disabledLabelColor = MaterialTheme.colorScheme.error
//                    )
//                else
//                    ExposedDropdownMenuDefaults.outlinedTextFieldColors(
//                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
//                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
//                        disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
//                    )
//        )
//    }
//}
//
//fun convertMillisToDate(millis: Long): String {
//    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//    return formatter.format(Date(millis))
//}
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DynamicSelectTextField(
//    selectedValue: String,
//    genderError: Boolean,
//    options: List<String>,
//    onValueChangedEvent: (String) -> Unit
//) {
//    var expanded by rememberSaveable { mutableStateOf(false) }
//
//    ExposedDropdownMenuBox(
//        expanded = expanded,
//        onExpandedChange = { expanded = !expanded }
//    ) {
//        OutlinedTextField(
//            readOnly = true,
//            value = selectedValue,
//            onValueChange = {},
//            label = {   Text(
//                text = stringResource(R.string.gender),
//                style = MaterialTheme.typography.bodyLarge,
//            )},
//            trailingIcon = {
//                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
//            },
//            isError = genderError,
//            shape = RoundedCornerShape(50.dp),
//            modifier = Modifier
//                .fillMaxWidth()
//                .menuAnchor(),
//            colors =  OutlinedTextFieldDefaults.colors(
//                focusedTextColor = MaterialTheme.colorScheme.onSurface,
//                focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f),
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
//                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
//                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
//                errorTextColor = MaterialTheme.colorScheme.error,
//                errorLabelColor = MaterialTheme.colorScheme.error,
//                errorBorderColor = MaterialTheme.colorScheme.error
//            )
//        )
//
//        ExposedDropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//            modifier = Modifier
//                .background(MaterialTheme.colorScheme.background),
//            shape = RoundedCornerShape(20.dp)
//        ) {
//            options.forEach { option: String ->
//                DropdownMenuItem(
//                    text = {
//                        Text(
//                            text = option,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .wrapContentHeight()
//                                .padding(horizontal = 5.dp),
//                            style = MaterialTheme.typography.bodyMedium,
//                        ) },
//                    onClick = {
//                        expanded = false
//                        onValueChangedEvent(option)
//
//                    }
//                )
//            }
//        }
//    }
//}
//
//
//@Composable
//private fun Gender(userData: RegisterScreenState, onAction: (RegisterAction) -> Unit = {}) {
//    var option by rememberSaveable { mutableStateOf("") }
//
//    DynamicSelectTextField(
//        selectedValue = option,
//        genderError = userData.errorGender,
//        options = listOf(
//            stringResource(R.string.male),
//            stringResource(R.string.female),
//            stringResource(R.string.xGender)
//        ),
//        onValueChangedEvent = { selectedOption ->
//            when (selectedOption) {
//                selectedOption -> {
//                    onAction(RegisterAction.OnGenderChange(selectedOption))
//                    option = selectedOption
//                }
//
//                else -> onAction(RegisterAction.OnGenderChange("X"))
//            }
//        }
//    )
//}

@Composable
private fun Password(userData: RegisterScreenState, onAction: (RegisterAction) -> Unit = {}) {
    TextField(
        value = userData.password,
        onValueChange = { onAction(RegisterAction.OnPasswordChange(it)) },
        label = stringResource(id = R.string.password),
        isError = userData.errorPassword,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        isPasswordField = true,
    )
}

@Composable
private fun ConfirmPassword(userData: RegisterScreenState, onAction: (RegisterAction) -> Unit = {}) {
    TextField(
        value = userData.confirmPassword,
        onValueChange = { onAction(RegisterAction.OnConfirmPasswordChange(it)) },
        label = stringResource(id = R.string.confirm_password),
        isError = userData.errorConfirmPassword,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        isPasswordField = true,
    )
}


@Preview
@Composable
fun RegisterPreview() {
    GenIATEATheme {
        RegisterScreen(
            meRegister,
            onAction = {}
        )
    }
}

@Preview
@Composable
fun RegisterPreviewEmpty() {
    GenIATEATheme {
        RegisterScreen(
            RegisterScreenState(),
            onAction = {}
        )
    }
}

@Preview
@Composable
fun RegisterPreviewDark() {
    GenIATEATheme(isDarkTheme = true) {
        RegisterScreen(
            meRegister,
            onAction = {}
        )
    }
}
