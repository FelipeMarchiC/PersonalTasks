package bes.mobile.personaltasks.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import bes.mobile.personaltasks.databinding.ActivityTaskFormBinding
import bes.mobile.personaltasks.model.Constant.EXTRA_TASK
import bes.mobile.personaltasks.model.Constant.EXTRA_VIEW_TASK
import bes.mobile.personaltasks.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskFormActivity: AppCompatActivity() {
    private val atfb: ActivityTaskFormBinding by lazy {
        ActivityTaskFormBinding.inflate(layoutInflater)
    }

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(atfb.root)

        setSupportActionBar(atfb.toolbarIn.toolbar)
        supportActionBar?.subtitle = "New Task"

        val receivedTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TASK, Task::class.java)
        }
        else {
            intent.getParcelableExtra(EXTRA_TASK)
        }

        receivedTask?.let{
            supportActionBar?.subtitle = "Edit Task"
            with(atfb) {
                editTitle.setText(it.title)
                editDescription.setText(it.description)
                textSelectedDate.text = formatDate(it.dueDate)

                val viewContact = intent.getBooleanExtra(EXTRA_VIEW_TASK, false)

                if (viewContact) {
                    supportActionBar?.subtitle = "View Task"
                    editTitle.isEnabled = false
                    editDescription.isEnabled = false
                    textSelectedDate.isEnabled = false
                    buttonSave.visibility = View.GONE
                }
            }
        }


        with(atfb) {
            buttonSave.setOnClickListener {
                Task(
                    receivedTask?.id?:hashCode(),
                    editTitle.text.toString(),
                    editDescription.text.toString(),
                    parseDate(textSelectedDate.text.toString()) ?: Date()
                ).let {
                    task -> Intent().apply {
                        putExtra(EXTRA_TASK, task)
                        setResult(RESULT_OK, this)
                    }
                }

                finish()
            }
        }
    }

    private fun formatDate(date: Date?): String {
        return date?.let { dateFormatter.format(it) } ?: ""
    }

    private fun parseDate(dateStr: String): Date? {
        return try {
            dateFormatter.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }
}