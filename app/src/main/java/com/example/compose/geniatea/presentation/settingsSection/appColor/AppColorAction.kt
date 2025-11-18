package com.example.compose.geniatea.presentation.settingsSection.appColor

interface AppColorAction {
    data object OnBackPressed : AppColorAction
    data object OnBluePressed : AppColorAction
    data object OnPinkPressed : AppColorAction
}