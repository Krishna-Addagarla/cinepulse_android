package com.partner.cinepulse.utils
import java.text.SimpleDateFormat
import java.util.Locale

fun formatBirthDate(birthDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)
        val date = inputFormat.parse(birthDate)
        outputFormat.format(date?:"")
    } catch (e: Exception) {
        birthDate // Return original if parsing fails
    }
}

