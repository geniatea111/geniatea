package com.example.compose.geniatea.presentation.userManagementSection.preregister

data class PreRegisterScreenState(
    val email: String = "",
    val errorEmail : Boolean = false,
    val error : Int? = null,
)
