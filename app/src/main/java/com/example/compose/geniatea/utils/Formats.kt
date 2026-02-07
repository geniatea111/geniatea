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

        @RequiresApi(Build.VERSION_CODES.O)
        fun formatFriendlyDate(isoDate: String?): String {
            if (isoDate == null) return ""
            return try {
                // Determine if it includes time (T)
                val inputFormatter = if (isoDate.contains("T")) {
                     DateTimeFormatter.ISO_DATE_TIME
                } else {
                     DateTimeFormatter.ofPattern("yyyy-MM-dd")
                }
                
                val dateTime = try {
                    java.time.LocalDateTime.parse(isoDate, inputFormatter)
                } catch (e: Exception) {
                    // Fallback if strictly date
                    LocalDate.parse(isoDate, inputFormatter).atStartOfDay()
                }

                val now = java.time.LocalDateTime.now()
                val today = java.time.LocalDate.now()
                val date = dateTime.toLocalDate()

                if (date.isEqual(today)) {
                    // Today: Show time
                    dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                } else if (date.isEqual(today.minusDays(1))) {
                    "Ayer"
                } else if (date.isAfter(today.minusDays(7))) {
                    // Last 7 days: Show day name
                    val dayName = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale("es", "ES"))
                    dayName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
                } else {
                    // Older: Show date
                    date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                }
            } catch (e: Exception) {
                isoDate // Return original if parsing fails
            }
        }
    }
}