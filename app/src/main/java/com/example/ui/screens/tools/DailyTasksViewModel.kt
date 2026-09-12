package com.example.ui.screens.tools

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.TaskEntity
import com.example.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DailyTasksViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    
    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.appDao())
    }

    val tasks: StateFlow<List<TaskEntity>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val newTaskTitle = MutableStateFlow("")

    fun onTitleChange(title: String) {
        newTaskTitle.value = title
    }

    fun addTask() {
        val title = newTaskTitle.value.trim()
        if (title.isNotEmpty()) {
            viewModelScope.launch {
                repository.addTask(title)
                newTaskTitle.value = ""
            }
        }
    }

    fun toggleTask(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(id, isCompleted)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }
}
