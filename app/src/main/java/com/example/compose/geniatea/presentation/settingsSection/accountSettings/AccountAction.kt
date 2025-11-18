package com.example.compose.geniatea.presentation.settingsSection.accountSettings

interface AccountAction {
    data object OnBackPressed : AccountAction
    data object OnUpdatePressed: AccountAction
    data class OnNameChange(val name: String): AccountAction
    data class OnBirthDateChange(val birthDate: String): AccountAction
    data class OnGenderChange(val gender: String): AccountAction
    data object OnPasswordUpdatePressed: AccountAction
    data object OnDeleteAccountPressed : AccountAction

    data object OnUpdateSuccess : AccountAction
    data object OnUpdateError: AccountAction
    data object OnDeleteAccountSuccess : AccountAction
    data object OnDeleteAccountError: AccountAction
    data class OnRefreshTokenSuccess(val accessToken:String, val refreshToken:String) : AccountAction
}