package bes.mobile.personaltasks.controller

import androidx.room.Room
import bes.mobile.personaltasks.view.MainActivity
import bes.mobile.personaltasks.model.Task
import bes.mobile.personaltasks.model.TaskDao
import bes.mobile.personaltasks.model.TaskFirebaseDb
import bes.mobile.personaltasks.model.TaskRoomDb

class TaskController(mainActivity: MainActivity) {
    /* private val taskDao: TaskDao = Room.databaseBuilder(
        mainActivity,
        TaskRoomDb::class.java,
        name = "task-database"
    ).build().taskDao() */

    private val taskDao: TaskDao = TaskFirebaseDb()

    fun createTask(task: Task) = taskDao.createTask(task)
    fun getTask(id: Int) = taskDao.retrieveTask(id)
    fun getTasks() = taskDao.retrieveTasks()
    fun updateTask(task: Task) = taskDao.updateTask(task)
    fun deleteTask(task: Task) = taskDao.deleteTask(task)
}