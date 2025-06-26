package bes.mobile.personaltasks.controller

import bes.mobile.personaltasks.model.Task
import bes.mobile.personaltasks.model.TaskDao
import bes.mobile.personaltasks.model.TaskFirebaseDb

class TaskController() {
    /* private val taskDao: TaskDao = Room.databaseBuilder(
        mainActivity,
        TaskRoomDb::class.java,
        name = "task-database"
    ).build().taskDao() */

    private val taskDao: TaskDao = TaskFirebaseDb()

    fun createTask(task: Task) = taskDao.createTask(task)
    fun getTask(id: Int) = taskDao.retrieveTask(id)

    /**
     * Busca as tarefas de forma assíncrona e retorna o resultado via callback.
     *
     * @param onResult Função lambda (callback) que será chamada com a lista de tarefas filtradas.
     *      O tipo dessa função é (List<Task>) -> Unit, ou seja:
     *      - Recebe uma lista de Task como parâmetro
     *      - Não retorna nenhum valor (Unit significa "sem retorno", void em Java)
     *
     * A função é executada dentro de uma Thread p/ não travar a UI.
     * Quando a busca e o filtro terminam, o callback é chamado para que quem chamou possa tratar o resultado.
    */
    fun getTasks(onResult: (List<Task>) -> Unit) {
        Thread {
            val taskList = taskDao.retrieveTasks()
            onResult(taskList)
        }.start()
    }

    fun getDeletedTasks(onResult: (List<Task>) -> Unit) {
        Thread {
            val taskList = taskDao.retrieveDeletedTasks()
            onResult(taskList)
        }.start()
    }

    fun updateTask(task: Task) = taskDao.updateTask(task)
    fun deleteTask(task: Task) = taskDao.deleteTask(task)
}