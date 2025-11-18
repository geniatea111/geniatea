package com.example.compose.geniatea.presentation.userManagementSection.register

data class RegisterScreenState(
   // val name: String = "",
    val username: String = "",
    val email: String = "",
//    val birthDate: String = "",
//    val gender: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    val errorForm : Int? = null,
   // val errorName : Boolean = false,
    val errorUsername : Boolean = false,
    val errorEmail : Boolean = false,
//    val errorBirthDate : Boolean = false,
//    val errorGender : Boolean = false,
    val errorPassword : Boolean = false,
    val errorConfirmPassword : Boolean = false,

    val error : String? = null
)
