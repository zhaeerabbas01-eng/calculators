package com.example.data.repository

import com.example.data.db.HistoryDao
import com.example.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<HistoryItem>> = historyDao.getAllHistory()
    val favorites: Flow<List<HistoryItem>> = historyDao.getFavorites()

    suspend fun insert(item: HistoryItem) = historyDao.insert(item)
    
    suspend fun toggleFavorite(item: HistoryItem) = historyDao.update(item.copy(isFavorite = !item.isFavorite))

    suspend fun deleteById(id: Int) = historyDao.deleteById(id)

    suspend fun clearAll() = historyDao.clearAll()
}
