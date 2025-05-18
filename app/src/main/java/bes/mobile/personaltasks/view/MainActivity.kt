package bes.mobile.personaltasks.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import bes.mobile.personaltasks.R
import bes.mobile.personaltasks.controller.TaskController
import bes.mobile.personaltasks.databinding.ActivityMainBinding
import bes.mobile.personaltasks.model.Constant.EXTRA_TASK
import bes.mobile.personaltasks.model.Constant.EXTRA_VIEW_TASK
import bes.mobile.personaltasks.model.Task

class MainActivity : AppCompatActivity(), OnTaskClickListener {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val taskList: MutableList<Task> = mutableListOf()

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
                val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(EXTRA_TASK, Task::class.java)
                }
                else {
                    result.data?.getParcelableExtra(EXTRA_TASK)
                }

                task?.let {
                    receivedTask ->
                    val position = taskList.indexOfFirst { it.id == receivedTask.id }
                    if (position == -1) {
                        taskList.add(receivedTask)
                        // TODO: ADAPTER
                        mainController.createTask(receivedTask)
                    }
                    else {
                        taskList[position] = receivedTask
                        // TODO: ADAPTER
                        mainController.updateTask(receivedTask)
                    }
                }
            }
        }

        //TODO: ADAPTER -> amb.taskRv.adapter = ?
        amb.taskRv.layoutManager = LinearLayoutManager(this)

        fillTaskList()
    }

    private fun fillTaskList() {
        taskList.clear()

        Thread {
            taskList.addAll(mainController.getContacts())
            //TODO: ADAPTER
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

    override fun onTaskClick(position: Int) {
        Intent(this, TaskFormActivity::class.java).apply {
            putExtra(EXTRA_TASK, taskList[position])
            putExtra(EXTRA_VIEW_TASK, true)
            startActivity(this)
        }
    }
}