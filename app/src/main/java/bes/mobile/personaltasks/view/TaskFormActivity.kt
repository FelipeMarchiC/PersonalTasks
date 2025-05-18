package bes.mobile.personaltasks.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import bes.mobile.personaltasks.databinding.ActivityTaskFormBinding
import bes.mobile.personaltasks.model.Constant.EXTRA_TASK
import bes.mobile.personaltasks.model.Constant.EXTRA_VIEW_TASK
import bes.mobile.personaltasks.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskFormActivity: AppCompatActivity() {
    private val atfb: ActivityTaskFormBinding by lazy {
        ActivityTaskFormBinding.inflate(layoutInflater)
    }

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var selectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(atfb.root)

        setSupportActionBar(atfb.toolbarIn.toolbar)
        supportActionBar?.subtitle = "New Task"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        onBackPressedDispatcher.addCallback(this) {
            setResult(RESULT_CANCELED)
            finish()
        }

        val task = getTaskFromIntent()

        if (task != null) prepareView(task)

        prepareButtonBehaviour(task)
    }

    private fun getTaskFromIntent(): Task? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TASK, Task::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_TASK)
        }
    }

    private fun prepareView(receivedTask: Task) {
        receivedTask.let {
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
                    buttonPickDate.isEnabled = false
                    buttonSave.visibility = View.GONE
                    buttonCancel.visibility = View.GONE
                }
            }
        }
    }

    private fun prepareButtonBehaviour(receivedTask: Task?) {
        with(atfb) {
            buttonPickDate.setOnClickListener {
                showDatePickerDialog()
            }

            buttonCancel.setOnClickListener {
                setResult(RESULT_CANCELED)
                finish()
            }

            buttonSave.setOnClickListener {
                Task(
                    receivedTask?.id ?: hashCode(),
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

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        selectedDate?.let {
            calendar.time = it
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, y, m, d ->
            val pickedCalendar = Calendar.getInstance()
            pickedCalendar.set(y, m, d)

            selectedDate = pickedCalendar.time
            atfb.textSelectedDate.text = formatDate(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
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