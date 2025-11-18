package com.example.compose.geniatea.presentation.userManagementSection.register

import com.example.compose.geniatea.domain.User

interface RegisterAction {
    data object OnBackPressed: RegisterAction
    data object OnRegisterClicked: RegisterAction
    data class OnUsernameChange(val username: String): RegisterAction
    data class OnEmailChange(val email: String): RegisterAction
    //data class OnNameChange(val name: String): RegisterAction
    //    data class OnBirthDateChange(val birthDate: String): RegisterAction
//    data class OnGenderChange(val gender: String): RegisterAction
    data class OnPasswordChange(val password: String): RegisterAction
    data class OnConfirmPasswordChange(val confirmPassword: String): RegisterAction

    class OnRegisterSuccess(val user: User): RegisterAction
    class OnRegisterError(val error: String): RegisterAction
}