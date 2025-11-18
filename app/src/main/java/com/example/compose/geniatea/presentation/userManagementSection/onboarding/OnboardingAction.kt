package com.example.compose.geniatea.presentation.userManagementSection.onboarding

sealed interface OnboardingAction {
    data object OnBackPressed: OnboardingAction
    data object OnSkipPressed: OnboardingAction
    data object OnContinuePressed: OnboardingAction
    data object OnGenerateImagePressed : OnboardingAction

    data class OnOptionSelectedPerson(val option: String): OnboardingAction
    data class OnOptionSelectedGoal(val option: String): OnboardingAction

}