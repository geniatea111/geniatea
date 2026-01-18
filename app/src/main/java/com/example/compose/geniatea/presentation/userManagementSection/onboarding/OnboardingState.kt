package com.example.compose.geniatea.presentation.userManagementSection.onboarding

data class OnboardingState(
    val name: String = "",
    val pronoun: String = "",
    val birthDate: String = "",
    val description: String = "",
    val showPictograms: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
)
