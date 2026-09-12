package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // History
    @Insert
    suspend fun insertHistory(history: HistoryEntity)

    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM calculation_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteHistory(): Flow<List<HistoryEntity>>
    
    @Query("UPDATE calculation_history SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteHistory(id: Long)

    @Query("DELETE FROM calculation_history")
    suspend fun clearHistory()

    // Currency Rates
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyRate(rate: CurrencyRateEntity)

    @Query("SELECT * FROM currency_rates WHERE pair = :pair")
    suspend fun getCurrencyRate(pair: String): CurrencyRateEntity?

    // Daily Tasks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Query("SELECT * FROM daily_tasks ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("UPDATE daily_tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean)

    @Query("DELETE FROM daily_tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)
}
