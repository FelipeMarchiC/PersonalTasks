package bes.mobile.personaltasks.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import bes.mobile.personaltasks.R
import bes.mobile.personaltasks.adapter.TaskHistoryRvAdapter
import bes.mobile.personaltasks.controller.TaskController
import bes.mobile.personaltasks.databinding.ActivityHistoryBinding
import bes.mobile.personaltasks.model.Constant.EXTRA_TASK
import bes.mobile.personaltasks.model.Constant.EXTRA_VIEW_TASK
import bes.mobile.personaltasks.model.Task

class HistoryActivity : AppCompatActivity(), OnTaskClickListener, OnDeletedTaskLongClickListener {
    // Binding da interface usando ViewBinding
    private val ahb: ActivityHistoryBinding by lazy {
        ActivityHistoryBinding.inflate(layoutInflater)
    }

    // Lista mutável de tarefas exibidas no RecyclerView
    private val taskList: MutableList<Task> = mutableListOf()

    // Adapter do RecyclerView, configurado com listeners
    private val taskAdapter: TaskHistoryRvAdapter by lazy {
        TaskHistoryRvAdapter(taskList, this, this)
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
    private val getTasksHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // Só executa a lógica se for a mensagem de atualização de tarefas
            if (msg.what == GET_TASKS_MESSAGE) {
                // Usa o método com callback para buscar tarefas async
                mainController.getDeletedTasks { tasks ->
                    // Atualiza a lista e o adapter na thread principal
                    runOnUiThread {
                        taskList.clear()
                        taskList.addAll(tasks)
                        taskAdapter.notifyDataSetChanged()
                    }
                }

                // Reenvia mensagem para manter atualizações constantes
                sendMessageDelayed(
                    obtainMessage().apply { what = GET_TASKS_MESSAGE },
                    GET_TASKS_INTERVAL
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ahb.root)
        setSupportActionBar(ahb.toolbarIn.toolbar)

        // Registra launcher para tratar retorno da TaskFormActivity
        cArl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        // Configura RecyclerView
        ahb.taskRv.adapter = taskAdapter
        ahb.taskRv.layoutManager = LinearLayoutManager(this)

        // Envia primeira mensagem para atualizar as tarefas
        getTasksHandler.sendMessage(
            Message().apply {
                what = GET_TASKS_MESSAGE
            }
        )
    }


    // Infla o menu da toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        return true
    }

    // Trata cliques nos itens do menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.go_home_mi -> {
                cArl.launch(Intent(this, MainActivity::class.java))
                true
            }
            else -> false
        }
    }

    // Cria intent para abrir formulário, somente para visualização
    private fun createTaskFormIntent(task: Task?): Intent {
        return Intent(this, TaskFormActivity::class.java).apply {
            putExtra(EXTRA_TASK, task)
            putExtra(EXTRA_VIEW_TASK, true)
        }
    }

    override fun onTaskClick(position: Int) {
        cArl.launch(createTaskFormIntent(taskList[position]))
    }

    override fun onDetailsTaskMenuItemClick(position: Int) {
        cArl.launch(createTaskFormIntent(taskList[position]))
    }

    // Remove tarefa da lista e a ativa de novo no banco de dados
    override fun onReactivateTaskMenuItemClick(position: Int) {
        val task = taskList[position]
        task.deletedAt = null

        taskList.removeAt(position)
        taskAdapter.notifyItemRemoved(position)

        Toast.makeText(this, "Tarefa reativada!", Toast.LENGTH_SHORT).show()
        Thread { mainController.updateTask(task) }.start()
    }
}