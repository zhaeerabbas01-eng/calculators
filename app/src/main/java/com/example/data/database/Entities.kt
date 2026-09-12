package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val toolName: String,
    val inputData: String,
    val expression: String,
    val result: String,
    val timestamp: Long,
    val isFavorite: Boolean = false
)

@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey val pair: String, // e.g., "USD_PKR"
    val rate: Double,
    val lastUpdated: Long
)

@Entity(tableName = "daily_tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val timestamp: Long
)
