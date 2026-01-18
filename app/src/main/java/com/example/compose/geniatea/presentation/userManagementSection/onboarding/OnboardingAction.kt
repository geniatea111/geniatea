package com.example.compose.geniatea.presentation.userManagementSection.onboarding

sealed interface OnboardingAction {
    data class OnNameChange(val name: String) : OnboardingAction
    data class OnPronounChange(val pronoun: String) : OnboardingAction
    data class OnBirthDateChange(val birthDate: String) : OnboardingAction
    data class OnDescriptionChange(val description: String) : OnboardingAction
    data class OnShowPictogramsChange(val show: Boolean) : OnboardingAction
    data object OnRegister : OnboardingAction
}
