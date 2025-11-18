package com.example.compose.geniatea.presentation.userManagementSection.onboarding

import android.net.Uri


data class OnboardingScreenState(
    val selectedOption: String = "",
    val selectedImage: Uri? = null
)
