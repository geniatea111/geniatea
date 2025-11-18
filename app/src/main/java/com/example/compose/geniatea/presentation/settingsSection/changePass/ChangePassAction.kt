package com.example.compose.geniatea.presentation.settingsSection.changePass

interface ChangePassAction {
    data object OnBackPressed : ChangePassAction
    data object OnChangePassClicked : ChangePassAction
    data class OnCurrentPassChange(val currentPass: String) : ChangePassAction
    data class OnNewPassChange(val newPass: String) : ChangePassAction
    data class OnConfirmNewPassChange(val confirmNewPass: String) : ChangePassAction

    data object OnChangePassSuccess : ChangePassAction
    data class OnChangePassError(val error: String) : ChangePassAction
}