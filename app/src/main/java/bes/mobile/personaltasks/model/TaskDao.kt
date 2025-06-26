package bes.mobile.personaltasks.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Insert
    fun createTask(task: Task): Long

    @Delete
    fun deleteTask(task: Task): Int

    @Update
    fun updateTask(task: Task): Int

    @Query("SELECT * FROM Task WHERE deletedAt IS NULL")
    fun retrieveTasks(): MutableList<Task>

    @Query("SELECT * FROM Task WHERE deletedAt IS NOT NULL")
    fun retrieveDeletedTasks(): List<Task>

    @Query("SELECT * FROM Task WHERE id = :id")
    fun retrieveTask(id: Int): Task
}