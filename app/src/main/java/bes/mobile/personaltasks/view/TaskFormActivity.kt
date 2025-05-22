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

    // Binding da interface usando ViewBinding
    private val atfb: ActivityTaskFormBinding by lazy {
        ActivityTaskFormBinding.inflate(layoutInflater)
    }

    // Formatador de data para datas no formato "dia/mes/ano"
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Data selecionada pelo usuário
    private var selectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(atfb.root)

        // Configura toolbar com botão de voltar
        setSupportActionBar(atfb.toolbarIn.toolbar)
        supportActionBar?.subtitle = "Nova Tarefa"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Define comportamento do botão de voltar do sistema
        onBackPressedDispatcher.addCallback(this) {
            setResult(RESULT_CANCELED)
            finish()
        }

        // Recupera tarefa passada pela intent, se houver
        val task = getTaskFromIntent()

        // Prepara a visualização, se estiver editando ou visualizando uma tarefa
        if (task != null) prepareView(task)

        // Configura o comportamento dos botões
        prepareButtonBehaviour(task)
    }

    // Recupera a Task da intent de forma compatível com diferentes versões do Android
    private fun getTaskFromIntent(): Task? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TASK, Task::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_TASK)
        }
    }

    // Preenche os campos da interface com os dados da tarefa recebida
    private fun prepareView(receivedTask: Task) {
        receivedTask.let {
            supportActionBar?.subtitle = "Editar Tarefa"
            with(atfb) {
                editTitle.setText(it.title)
                editDescription.setText(it.description)
                textSelectedDate.text = formatDate(it.dueDate)

                // Verifica se a tarefa está sendo apenas visualizada
                val viewContact = intent.getBooleanExtra(EXTRA_VIEW_TASK, false)

                if (viewContact) {
                    supportActionBar?.subtitle = "Ver Tarefa"
                    // Desabilita campos e esconde botões
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

    // Define os comportamentos dos botões de data, salvar e cancelar
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
                // Cria nova Task com os dados preenchidos
                Task(
                    receivedTask?.id ?: hashCode(), // Se nova, gera ID com hashCode
                    editTitle.text.toString(),
                    editDescription.text.toString(),
                    parseDate(textSelectedDate.text.toString()) ?: Date()
                ).let { task ->
                    // Retorna a task como resultado para a Activity anterior
                    Intent().apply {
                        putExtra(EXTRA_TASK, task)
                        setResult(RESULT_OK, this)
                    }
                }

                finish()
            }
        }
    }

    // Mostra o DatePicker para o usuário escolher uma data
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

    // Trata clique no botão "voltar" da toolbar
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

    // Formata uma data para string
    private fun formatDate(date: Date?): String {
        return date?.let { dateFormatter.format(it) } ?: ""
    }

    // Converte uma string para objeto Date
    private fun parseDate(dateStr: String): Date? {
        return try {
            dateFormatter.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }
}