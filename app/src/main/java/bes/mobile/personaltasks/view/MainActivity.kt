package bes.mobile.personaltasks.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import bes.mobile.personaltasks.model.Constant.EXTRA_VIEW_TASK
import bes.mobile.personaltasks.model.Task

class MainActivity : AppCompatActivity(), OnTaskClickListener, OnTaskLongClickListener {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val taskList: MutableList<Task> = mutableListOf()

    private val taskAdapter: TaskRvAdapter by lazy {
        TaskRvAdapter(taskList, this, this)
    }

    private lateinit var cArl: ActivityResultLauncher<Intent>

    private val mainController: TaskController by lazy {
        TaskController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        setSupportActionBar(amb.toolbarIn.toolbar)

        cArl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            if (result.resultCode == RESULT_OK) {
                val task = getTaskFrom(result)
                task?.let(::handleReceivedTask)
            }
        }

        amb.taskRv.adapter = taskAdapter
        amb.taskRv.layoutManager = LinearLayoutManager(this)

        fillTaskList()
    }

    private fun getTaskFrom(result: ActivityResult) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            result.data?.getParcelableExtra(EXTRA_TASK, Task::class.java)
        } else {
            result.data?.getParcelableExtra(EXTRA_TASK)
        }

    private fun handleReceivedTask(receivedTask: Task) {
        val position = taskList.indexOfFirst { it.id == receivedTask.id }

        if (position == -1) {
            taskList.add(receivedTask)
            taskAdapter.notifyItemInserted(taskList.lastIndex)

            Thread { mainController.createTask(receivedTask) }.start()
        } else {
            taskList[position] = receivedTask
            taskAdapter.notifyItemChanged(position)

            Thread { mainController.updateTask(receivedTask) }.start()
        }
    }

    private fun fillTaskList() {
        Thread {
            val tasks = mainController.getTasks()

            runOnUiThread {
                taskList.clear()
                taskList.addAll(tasks)
                taskAdapter.notifyDataSetChanged()
            }
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.add_task_mi -> {
                cArl.launch(Intent(this, TaskFormActivity::class.java))
                true
            }
            else -> { false }
        }
    }

    private fun createTaskFormIntent(task: Task?, viewOnly: Boolean = false): Intent {
        return Intent(this, TaskFormActivity::class.java).apply {
            putExtra(EXTRA_TASK, task)
            putExtra(EXTRA_VIEW_TASK, viewOnly)
        }
    }

    override fun onTaskClick(position: Int) {
        startActivity(createTaskFormIntent(taskList[position], viewOnly = true))
    }

    override fun onDetailsTaskMenuItemClick(position: Int) {
        cArl.launch(createTaskFormIntent(taskList[position], viewOnly = true))
    }

    override fun onEditTaskMenuItemClick(position: Int) {
        cArl.launch(createTaskFormIntent(taskList[position]))
    }

    override fun onRemoveTaskMenuItemClick(position: Int) {
        val task = taskList[position]
        taskList.removeAt(position)
        taskAdapter.notifyItemRemoved(position)
        Toast.makeText(this, "Task removed!", Toast.LENGTH_SHORT).show()

        Thread { mainController.deleteTask(task) }.start()
    }
}