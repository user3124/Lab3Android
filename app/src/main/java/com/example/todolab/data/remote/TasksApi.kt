package com.example.todolab.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TasksApi {

    @GET("todos")
    suspend fun getTodos(@Query("_limit") limit: Int = 10): List<RemoteTask>

    // Фейковый API принимает запрос, но данные не сохраняет
    @POST("todos")
    suspend fun createTodo(@Body task: RemoteTask): RemoteTask
}