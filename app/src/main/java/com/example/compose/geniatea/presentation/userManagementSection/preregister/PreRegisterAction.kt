package com.example.compose.geniatea.presentation.userManagementSection.preregister

import com.example.compose.geniatea.domain.User

interface PreRegisterAction {
    data object OnBackPressed: PreRegisterAction
    data object OnContinueClicked: PreRegisterAction
    data class OnEmailChange(val email: String): PreRegisterAction
    data object OnLoginGoogleClicked: PreRegisterAction


    class OnEmailSuccess(val user: User): PreRegisterAction
    class OnEmailError(val error: String): PreRegisterAction
}