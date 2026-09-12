package com.example.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.domain.model.HistoryItem

@Database(entities = [HistoryItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
