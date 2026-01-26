package com.example.compose.geniatea.presentation.settingsSection.aiSettings

interface AISettingsAction {
    data object OnBackPressed : AISettingsAction
    data class OnClearLanguageToggle(val isChecked: Boolean) : AISettingsAction
    data class OnResponseStyleChange(val value: Float) : AISettingsAction
    data class OnFontSizeChange(val size: Int) : AISettingsAction
    data class OnAvatarSourceChange(val source: AvatarSource) : AISettingsAction
    data object OnSavePressed : AISettingsAction
}