package com.example.compose.geniatea.utils

import android.util.Patterns

class TextFielValidity{
    companion object {

        fun isnotValidPassword(password: String): Boolean {
            return !Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$").matches(password)
        }

        fun isnotValidEmail(email: String): Boolean {
            return !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun notsamePasswords(password: String, confirmPassword: String): Boolean {
            return password != confirmPassword
        }
    }
}