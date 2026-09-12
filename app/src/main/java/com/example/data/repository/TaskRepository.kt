package com.example.data.repository

import com.example.data.database.AppDao
import com.example.data.database.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: AppDao) {

    fun getAllTasks(): Flow<List<TaskEntity>> = dao.getAllTasks()

    suspend fun addTask(title: String) {
        val task = TaskEntity(
            title = title,
            isCompleted = false,
            timestamp = System.currentTimeMillis()
        )
        dao.insertTask(task)
    }

    suspend fun toggleTaskCompletion(id: Long, isCompleted: Boolean) {
        dao.updateTaskCompletion(id, isCompleted)
    }

    suspend fun deleteTask(id: Long) {
        dao.deleteTask(id)
    }
}
