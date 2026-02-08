package com.example.compose.geniatea.presentation.userManagementSection.onboarding

sealed interface OnboardingAction {
    data class OnNameChange(val name: String) : OnboardingAction
    data class OnPronounChange(val pronoun: String) : OnboardingAction
    data class OnBirthDateChange(val birthDate: String) : OnboardingAction
    data class OnDescriptionChange(val description: String) : OnboardingAction
    data class OnShowPictogramsChange(val show: Boolean) : OnboardingAction
    data class OnAvatarSourceChange(val source: com.example.compose.geniatea.presentation.settingsSection.aiSettings.AvatarSource) : OnboardingAction
    data class OnAvatarSelected(val uri: android.net.Uri, val context: android.content.Context) : OnboardingAction
    data class OnAvatarVideoSelected(val uri: android.net.Uri, val context: android.content.Context) : OnboardingAction
    data object OnRegister : OnboardingAction
}
