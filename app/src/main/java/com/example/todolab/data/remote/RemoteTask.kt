package com.example.todolab.data.remote

import com.example.todolab.data.Task

// Формат задачи, который отдаёт jsonplaceholder
data class RemoteTask(
    val userId: Int,
    val id: Long,
    val title: String,
    val completed: Boolean
)

fun RemoteTask.toTask(): Task = Task(id = id, title = title, completed = completed)

fun Task.toRemote(): RemoteTask = RemoteTask(userId = 1, id = id, title = title, completed = completed)