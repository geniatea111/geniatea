package com.example.compose.geniatea.presentation.settingsSection.accountSettings

data class AccountState (
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val birthDate: String = "",
    val gender: String = "",

    val errorForm : Int? = null,
    val errorName : Boolean = false,
    val errorUsername : Boolean = false,
    val errorBirthDate : Boolean = false,
    val errorGender : Boolean = false,
)