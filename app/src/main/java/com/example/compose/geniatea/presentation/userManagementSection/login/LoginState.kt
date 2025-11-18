package com.example.compose.geniatea.presentation.userManagementSection.login

data class LoginScreenState(
    val username: String = "",
    val password: String = "",
    val success: Boolean = false,
    val error: String = "",
    val errorForm : Int? = null,
    val errorUsername : Boolean = false,
    val errorPassword : Boolean = false

)
