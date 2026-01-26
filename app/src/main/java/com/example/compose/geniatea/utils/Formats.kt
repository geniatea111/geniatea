package com.example.compose.geniatea.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.compose.geniatea.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Formats {
    companion object {
        fun formatGenderToBack(gender: String, context: Context): String {
            return when (gender) {
                context.getString(R.string.female) -> "F"
                context.getString(R.string.male) -> "M"
                else -> "X"
            }
        }

        fun formatGender(gender: String, context: Context) : String {
            return when (gender) {
                "F" -> context.getString(R.string.female)
                "M" -> context.getString(R.string.male)
                else -> context.getString(R.string.xGender)
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