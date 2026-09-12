package com.example.domain.engine

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateTimeEngine {
    
    fun calculateDifference(startDateMillis: Long, endDateMillis: Long): String {
        val diffInMillies = Math.abs(endDateMillis - startDateMillis)
        
        val days = TimeUnit.MILLISECONDS.toDays(diffInMillies)
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillies) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillies) % 60
        
        val years = days / 365
        val remainingDays = days % 365
        val months = remainingDays / 30
        val finalDays = remainingDays % 30
        
        val parts = mutableListOf<String>()
        if (years > 0) parts.add("$years years")
        if (months > 0) parts.add("$months months")
        if (finalDays > 0) parts.add("$finalDays days")
        if (hours > 0) parts.add("$hours hours")
        if (minutes > 0) parts.add("$minutes minutes")
        
        return if (parts.isEmpty()) "0 minutes" else parts.joinToString(", ")
    }
    
    fun addOrSubtract(
        dateMillis: Long,
        years: Int = 0,
        months: Int = 0,
        days: Int = 0,
        isAddition: Boolean = true
    ): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis
        
        val factor = if (isAddition) 1 else -1
        
        calendar.add(Calendar.YEAR, years * factor)
        calendar.add(Calendar.MONTH, months * factor)
        calendar.add(Calendar.DAY_OF_MONTH, days * factor)
        
        return calendar.timeInMillis
    }
    
    fun formatDate(millis: Long): String {
        val sdf = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(millis))
    }
}
