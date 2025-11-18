package com.example.compose.geniatea.presentation.settingsSection.settings


interface SettingsAction {
    data object OnBackPressed : SettingsAction
    data object OnAccountPressed : SettingsAction
    data object OnLogoutPressed : SettingsAction
    data object OnAISettingsPressed : SettingsAction
    data object OnLocalizationsPressed : SettingsAction
    data object OnAboutPressed : SettingsAction
    data object OnPrivacyPolicyPressed : SettingsAction
    data class OnDarkModeToggle(val isChecked: Boolean) : SettingsAction
    data class OnAnimationsToggle(val isChecked: Boolean) : SettingsAction
    data class OnPictogramsToggle(val isChecked: Boolean) : SettingsAction
    data object OnNotificationPress : SettingsAction
    data class OnLanguagePress(val language : String) : SettingsAction
    data object OnAppIconPressed : SettingsAction
    data object OnAppColorPressed : SettingsAction
}