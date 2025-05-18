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

    @Query("SELECT * FROM Task")
    fun retrieveTasks(): MutableList<Task>

    @Query("SELECT * FROM Task WHERE id = :id")
    fun retrieveTask(id: Int): Task
}