package com.example.compose.geniatea.presentation.settingsSection.settings

data class SettingsScreenState(
    val isDarkMode: Boolean = false,
    val isPictosEnabled: Boolean = true,
    val isAnimationsEnabled: Boolean = true,
)