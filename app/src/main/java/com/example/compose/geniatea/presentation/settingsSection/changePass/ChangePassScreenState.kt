package com.example.compose.geniatea.presentation.settingsSection.changePass

data class ChangePassScreenState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",

    val errorCurrentPassword: Boolean = false,
    val errorNewPassword: Boolean = false,
    val errorConfirmNewPassword: Boolean = false,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: Int? = null
) {
    fun isFormValid(): Boolean {
        return currentPassword.isNotBlank() &&
                newPassword.isNotBlank() &&
                confirmNewPassword.isNotBlank() &&
                newPassword == confirmNewPassword
    }
}
