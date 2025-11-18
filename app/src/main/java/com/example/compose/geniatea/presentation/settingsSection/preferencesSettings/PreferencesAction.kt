package com.example.compose.geniatea.presentation.settingsSection.preferencesSettings

interface PreferencesAction {
    data object OnBackPressed : PreferencesAction
    data class OnDarkModeToggle(val isChecked: Boolean) : PreferencesAction
    data object OnNotificationPress : PreferencesAction
    data class OnLanguagePress(val language : String) : PreferencesAction
}