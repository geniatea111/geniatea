package com.example.compose.geniatea.presentation.userManagementSection.login

import com.example.compose.geniatea.domain.User

sealed interface LoginAction {
    data object OnBackPressed: LoginAction
    data object OnLoginClicked: LoginAction
    data object OnLoginGoogleClicked: LoginAction
    data object OnRegisterClicked : LoginAction
    class OnLoginSuccess(val user: User) : LoginAction
    data class OnUsernameChange(val username: String): LoginAction
    data class OnPasswordChange(val password: String): LoginAction
    class OnLoginError(val error: String): LoginAction
}