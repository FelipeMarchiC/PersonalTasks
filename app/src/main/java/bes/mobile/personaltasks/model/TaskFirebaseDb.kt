package bes.mobile.personaltasks.model

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Date

class TaskFirebaseDb : TaskDao {
    private val dbReference = Firebase.database.getReference("taskList")
    private val taskList = mutableListOf<Task>()

    init {
        dbReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val task = snapshot.getValue<Task>()

                // Garante id não nulo e único
                task?.let { newTask ->
                    if (!taskList.any { it.id == newTask.id }) {
                        taskList.add(newTask)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val task = snapshot.getValue<Task>()

                // Atualiza a tarefa na lista
                task?.let { editedTask ->
                    taskList[taskList.indexOfFirst { it.id == editedTask.id }] = editedTask
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val task = snapshot.getValue<Task>()

                // Remove a tarefa da lista
                task?.let {
                    taskList.remove(it)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Callback não aplicável no contexto atual
            }

            override fun onCancelled(error: DatabaseError) {
                // Callback não aplicável no contexto atual
            }

        })

        dbReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskMap = snapshot.getValue<Map<String, Task>>()

                // Atualiza a lista
                taskList.clear()
                taskMap?.values?.also { taskList.addAll(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                // Callback não aplicável no contexto atual
            }

        })
    }

    // Cria uma nova tarefa
    override fun createTask(task: Task): Long {
        dbReference.child(task.id.toString()).setValue(task)
        return 1L
    }

    // Retorna uma tarefa
    override fun retrieveTask(id: Int): Task {
        return taskList[taskList.indexOfFirst { it.id == id }]
    }

    // Retorna todas as tarefas, exceto deletadas
    override fun retrieveTasks(): MutableList<Task> {
        return taskList.filter { it.deletedAt == null }.toMutableList()
    }

    // Retorna todas as tarefas deletadas
    override fun retrieveDeletedTasks(): List<Task> {
        return taskList.filter { it.deletedAt != null }
    }

    // Atualiza uma tarefa no banco
    override fun updateTask(task: Task): Int {
        dbReference.child(task.id.toString()).setValue(task)
        return 1
    }

    // Marca uma tarefa como deletada (soft delete)
    override fun deleteTask(task: Task): Int {
        task.deletedAt = Date()
        dbReference.child(task.id.toString()).setValue(task)
        return 1
    }
}