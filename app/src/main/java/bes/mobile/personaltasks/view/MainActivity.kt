package bes.mobile.personaltasks.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import bes.mobile.personaltasks.R
import bes.mobile.personaltasks.adapter.TaskRvAdapter
import bes.mobile.personaltasks.controller.TaskController
import bes.mobile.personaltasks.databinding.ActivityMainBinding
import bes.mobile.personaltasks.model.Constant.EXTRA_TASK
import bes.mobile.personaltasks.model.Constant.EXTRA_TASK_ARRAY
import bes.mobile.personaltasks.model.Constant.EXTRA_VIEW_TASK
import bes.mobile.personaltasks.model.Task

class MainActivity : AppCompatActivity(), OnTaskClickListener, OnTaskLongClickListener {
    // Comment for test commit
    // Binding da interface usando ViewBinding
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // Lista mutável de tarefas exibidas no RecyclerView
    private val taskList: MutableList<Task> = mutableListOf()

    // Adapter do RecyclerView, configurado com listeners
    private val taskAdapter: TaskRvAdapter by lazy {
        TaskRvAdapter(taskList, this, this)
    }

    // Launcher para receber resultado da Activity de formulário
    private lateinit var cArl: ActivityResultLauncher<Intent>

    // Controller responsável pelas operações com dados
    private val mainController: TaskController by lazy {
        TaskController()
    }

    // Variáveis de controle para atualização de lista de tarefas
    companion object {
        const val GET_TASKS_MESSAGE = 1
        const val GET_TASKS_INTERVAL = 2000L
    }

    // Extensão da classe Handler
    private val getTasksHandler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            Thread {
                super.handleMessage(msg)

                if (msg.what == GET_TASKS_MESSAGE) {
                    mainController.getTasks()

                    // Reenvia mensagem para manter atualizações constantes
                    sendMessageDelayed(
                        obtainMessage().apply { what = GET_TASKS_MESSAGE },
                        GET_TASKS_INTERVAL
                    )
                } else {
                    val taskArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        msg.data?.getParcelableArray(EXTRA_TASK_ARRAY, Task::class.java)
                    } else {
                        msg.data?.getParcelableArray(EXTRA_TASK_ARRAY)
                    }

                    // limpa as tarefas e as adiciona novamente
                    runOnUiThread {
                        taskList.clear()
                        taskArray?.forEach { taskList.add(it as Task) }
                        taskAdapter.notifyDataSetChanged()
                    }
                }
            }.start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        setSupportActionBar(amb.toolbarIn.toolbar)

        // Registra launcher para tratar retorno da TaskFormActivity
        cArl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = getTaskFrom(result)
                task?.let(::handleReceivedTask)
            }
        }

        // Configura RecyclerView
        amb.taskRv.adapter = taskAdapter
        amb.taskRv.layoutManager = LinearLayoutManager(this)

        // Envia primeira mensagem para atualizar as tarefas
        getTasksHandler.sendMessageDelayed(
            Message().apply {
                what = GET_TASKS_MESSAGE
            }, GET_TASKS_INTERVAL
        )
    }

    // Recupera a Task do Intent, com suporte a diferentes versões do Android
    private fun getTaskFrom(result: ActivityResult) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            result.data?.getParcelableExtra(EXTRA_TASK, Task::class.java)
        } else {
            result.data?.getParcelableExtra(EXTRA_TASK)
        }

    // Trata tarefa recebida do formulário (inserção ou atualização)
    private fun handleReceivedTask(receivedTask: Task) {
        val position = taskList.indexOfFirst { it.id == receivedTask.id }

        if (position == -1) {
            // Nova tarefa
            taskList.add(receivedTask)
            taskAdapter.notifyItemInserted(taskList.lastIndex)
            Thread { mainController.createTask(receivedTask) }.start()
        } else {
            // Atualização de tarefa existente
            taskList[position] = receivedTask
            taskAdapter.notifyItemChanged(position)
            Thread { mainController.updateTask(receivedTask) }.start()
        }
    }

    // Infla o menu da toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Trata cliques nos itens do menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.add_task_mi -> {
                cArl.launch(Intent(this, TaskFormActivity::class.java))
                true
            }
            R.id.task_history_mi -> {
                cArl.launch(Intent(this, HistoryActivity::class.java))
                true
            }
            else -> false
        }
    }

    // Cria intent para abrir formulário, com opções de edição ou visualização
    private fun createTaskFormIntent(task: Task?, viewOnly: Boolean = false): Intent {
        return Intent(this, TaskFormActivity::class.java).apply {
            putExtra(EXTRA_TASK, task)
            putExtra(EXTRA_VIEW_TASK, viewOnly)
        }
    }

    // Abre formulário no modo de visualização ao clicar em uma tarefa
    override fun onTaskClick(position: Int) {
        startActivity(createTaskFormIntent(taskList[position], viewOnly = true))
    }

    // Abre detalhes da tarefa pelo menu de contexto
    override fun onDetailsTaskMenuItemClick(position: Int) {
        cArl.launch(createTaskFormIntent(taskList[position], viewOnly = true))
    }

    // Abre formulário para edição da tarefa
    override fun onEditTaskMenuItemClick(position: Int) {
        cArl.launch(createTaskFormIntent(taskList[position]))
    }

    // Remove tarefa da lista e do banco de dados
    override fun onRemoveTaskMenuItemClick(position: Int) {
        val task = taskList[position]
        taskList.removeAt(position)
        taskAdapter.notifyItemRemoved(position)
        Toast.makeText(this, "Tarefa removida!", Toast.LENGTH_SHORT).show()
        Thread { mainController.deleteTask(task) }.start()
    }
}