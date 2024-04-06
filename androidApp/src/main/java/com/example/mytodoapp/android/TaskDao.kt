package com.example.mytodoapp.android

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {
    @Insert
    fun insert(task: Task)

    @Query("SELECT * FROM tasks WHERE state = :state")
    fun getTasksByState(state: String): List<Task>

    @Query("SELECT * FROM tasks")
    fun getAll(): List<Task>

    @Query("DELETE FROM tasks WHERE id = :id")
    fun deleteById(id: Int)

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskById(id: Int): Task

    @Query("UPDATE tasks SET title = :title, deadline = :deadline, description = :description, state = :state WHERE id = :id")
    fun updateTask(id: Int, title: String, deadline: String, description: String, state: String)

    @Query("UPDATE tasks SET state = :state WHERE id = :id")
    fun updateState(id: Int, state: String)

    fun searchTasksByTitle(title: String): List<Task> {
        return getAll().filter { it.title.contains(title) }
    }
}