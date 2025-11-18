package com.example.compose.geniatea.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Formats {
    companion object {
        fun formatGenderToBack(gender: String): String {
            return when (gender) {
                "Mujer" -> "F"
                "Hombre" -> "M"
                else -> "X"
            }
        }

        fun formatGender(gender: String) : String {
            return when (gender) {
                "F" -> "Mujer"
                "M" -> "Hombre"
                else -> "Prefiero no decir"
            }
        }

        fun formatDateToBack(isoDate: String?): String {
            if (isoDate == null) return ""
            return try {
                val outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val inputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val date = LocalDate.parse(isoDate, inputFormat)
                date.format(outputFormat)
            } catch (e: Exception) {
                isoDate
            }
        }

        fun formatDate(isoDate: String?): String {
            if (isoDate == null) return ""
            return try {
                val outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val date = LocalDate.parse(isoDate, inputFormat)
                date.format(outputFormat)
            } catch (e: Exception) {
                isoDate
            }
        }
    }
}