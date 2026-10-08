package com.example.todolab.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolab.data.AppDatabase
import com.example.todolab.data.SyncPrefs
import com.example.todolab.data.Task
import com.example.todolab.work.SyncScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TasksViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getInstance(app).taskDao()

    val tasks: StateFlow<List<Task>> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val lastSync: StateFlow<Long> = SyncPrefs.lastSync

    init {
        SyncPrefs.load(app)
    }

    fun addTask(title: String) {
        val clean = title.trim()
        if (clean.isEmpty()) return
        viewModelScope.launch { dao.insert(Task(title = clean)) }
    }

    fun toggle(task: Task) {
        viewModelScope.launch { dao.update(task.copy(completed = !task.completed)) }
    }

    fun delete(task: Task) {
        viewModelScope.launch { dao.delete(task) }
    }

    fun syncNow() {
        SyncScheduler.syncNow(getApplication())
    }
}