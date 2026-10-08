package com.example.todolab.data

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // Для UI: список обновляется автоматически
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun observeAll(): Flow<List<Task>>

    // Для воркера и обычной работы
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    suspend fun getAll(): List<Task>

    // Для ContentProvider: Room умеет отдавать Cursor напрямую
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllCursor(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    // Синхронный вариант для ContentProvider (вызывается не из корутины)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBlocking(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<Task>)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}