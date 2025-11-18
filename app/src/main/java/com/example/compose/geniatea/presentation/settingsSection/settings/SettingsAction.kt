package com.example.compose.geniatea.presentation.settingsSection.settings



interface SettingsAction {
    data object OnBackPressed : SettingsAction
    data object OnAccountPressed : SettingsAction
    data object OnLogoutPressed : SettingsAction
    data object OnAISettingsPressed : SettingsAction
    data object OnPreferencesPressed : SettingsAction
    data object OnLocalizationsPressed : SettingsAction
    data object OnAboutPressed : SettingsAction
    data object OnPrivacyPolicyPressed : SettingsAction
}