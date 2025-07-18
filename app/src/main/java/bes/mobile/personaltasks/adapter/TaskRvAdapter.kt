package bes.mobile.personaltasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import bes.mobile.personaltasks.R
import bes.mobile.personaltasks.databinding.TaskCardBinding
import bes.mobile.personaltasks.model.Task
import bes.mobile.personaltasks.model.TaskPriority
import bes.mobile.personaltasks.view.OnTaskClickListener
import bes.mobile.personaltasks.view.OnTaskLongClickListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Adaptador para RecyclerView exibindo uma lista de tarefas com suporte a clique e menu de contexto
class TaskRvAdapter(
    private val taskList: MutableList<Task>,
    private val onTaskClickListener: OnTaskClickListener,
    private val onTaskLongClickListener: OnTaskLongClickListener,
): RecyclerView.Adapter<TaskRvAdapter.TaskViewHolder>() {

    // Formatador de data para datas no formato "dia/mes/ano"
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // ViewHolder interno contendo as views de um item da lista
    inner class TaskViewHolder(tcb: TaskCardBinding): RecyclerView.ViewHolder(tcb.root){
        val titleTv: TextView = tcb.taskTitle
        val descriptionTv: TextView = tcb.taskDescription
        val dueDateTv: TextView = tcb.taskDueDate
        val taskPriorityTv: TextView = tcb.taskPriority
        val isDoneTv: TextView = tcb.taskIsDone

        init {
            // Define menu de contexto com ações (detalhar, editar, remover)
            tcb.root.setOnCreateContextMenuListener { menu, _, _ ->
                (onTaskLongClickListener as AppCompatActivity).menuInflater.inflate(R.menu.context_menu_main, menu)

                menu.findItem(R.id.menu_details).setOnMenuItemClickListener {
                    onTaskLongClickListener.onDetailsTaskMenuItemClick(adapterPosition)
                    true
                }

                menu.findItem(R.id.menu_edit).setOnMenuItemClickListener {
                    onTaskLongClickListener.onEditTaskMenuItemClick(adapterPosition)
                    true
                }

                menu.findItem(R.id.menu_delete).setOnMenuItemClickListener {
                    onTaskLongClickListener.onRemoveTaskMenuItemClick(adapterPosition)
                    true
                }
            }

            // Define clique simples no item
            tcb.root.setOnClickListener {
                onTaskClickListener.onTaskClick(adapterPosition)
            }
        }
    }

    // Cria o ViewHolder a partir do layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            TaskCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // Preenche os dados da tarefa nas views do ViewHolder
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        taskList[position].let { task ->
            with(holder) {
                titleTv.text = task.title
                descriptionTv.text = task.description
                dueDateTv.text = formatDate(task.dueDate)
                taskPriorityTv.text = setPriorityText(task.priority);
                isDoneTv.text = setDoneText(task.done)
            }
        }
    }

    private fun setPriorityText(priority: TaskPriority): String {
        if (priority == TaskPriority.HIGH) {
            return "Alta Prioridade"
        }

        if (priority == TaskPriority.LOW) {
            return  "Baixa Prioridade"
        }

        return "Média Prioridade"
    }

    private fun setDoneText(isDone: Boolean): String {
        if (isDone) {
            return "Terminada"
        }

        return "Pendente"
    }

    // Retorna o número de itens na lista
    override fun getItemCount(): Int = taskList.size

    // Formata a data da tarefa
    private fun formatDate(date: Date?): String {
        return date?.let { dateFormatter.format(it) } ?: ""
    }
}
