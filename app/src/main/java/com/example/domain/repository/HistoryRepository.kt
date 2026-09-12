package com.example.domain.repository

import com.example.data.database.AppDao
import com.example.data.database.HistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: AppDao) {
    fun getAllHistory(): Flow<List<HistoryEntity>> = dao.getAllHistory()
    fun getFavoriteHistory(): Flow<List<HistoryEntity>> = dao.getFavoriteHistory()

    suspend fun addHistory(toolName: String, inputData: String, expression: String, result: String) {
        dao.insertHistory(
            HistoryEntity(
                toolName = toolName,
                inputData = inputData,
                expression = expression,
                result = result,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleFavorite(id: Long, currentStatus: Boolean) {
        dao.updateFavorite(id, !currentStatus)
    }

    suspend fun deleteHistory(id: Long) {
        dao.deleteHistory(id)
    }

    suspend fun clearAllHistory() {
        dao.clearHistory()
    }
}
