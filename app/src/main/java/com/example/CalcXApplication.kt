package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.db.AppDatabase
import com.example.data.repository.HistoryRepository

class CalcXApplication : Application() {

    lateinit var database: AppDatabase
    lateinit var historyRepository: HistoryRepository

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "calcx_database"
        ).build()
        
        historyRepository = HistoryRepository(database.historyDao())
    }
}
