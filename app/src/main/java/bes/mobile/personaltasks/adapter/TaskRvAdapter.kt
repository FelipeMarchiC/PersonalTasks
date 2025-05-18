package bes.mobile.personaltasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import bes.mobile.personaltasks.R
import bes.mobile.personaltasks.databinding.TaskCardBinding
import bes.mobile.personaltasks.model.Task
import bes.mobile.personaltasks.view.OnTaskClickListener
import bes.mobile.personaltasks.view.OnTaskLongClickListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskRvAdapter(
    private val taskList: MutableList<Task>,
    private val onTaskClickListener: OnTaskClickListener,
    private val onTaskLongClickListener: OnTaskLongClickListener,
): RecyclerView.Adapter<TaskRvAdapter.TaskViewHolder>()  {
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    inner class TaskViewHolder(tcb: TaskCardBinding): RecyclerView.ViewHolder(tcb.root){
        val titleTv: TextView = tcb.taskTitle
        val descriptionTv: TextView = tcb.taskDescription
        val dueDateTv: TextView = tcb.taskDueDate

        init {

            tcb.root.setOnCreateContextMenuListener{ menu, _, _ ->
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

            tcb.root.setOnClickListener { onTaskClickListener.onTaskClick(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder = TaskViewHolder(
        TaskCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {
        taskList[position].let { task ->
            with(holder) {
                titleTv.text = task.title
                descriptionTv.text = task.description
                dueDateTv.text = formatDate(task.dueDate)
            }
        }
    }

    override fun getItemCount(): Int = taskList.size

    private fun formatDate(date: Date?): String {
        return date?.let { dateFormatter.format(it) } ?: ""
    }
}