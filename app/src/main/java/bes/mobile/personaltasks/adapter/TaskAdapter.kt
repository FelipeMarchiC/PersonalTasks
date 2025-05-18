package bes.mobile.personaltasks.adapter

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import bes.mobile.personaltasks.R
import bes.mobile.personaltasks.databinding.TaskCardBinding
import bes.mobile.personaltasks.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(context: Context, private val taskList: MutableList<Task>) : ArrayAdapter<Task>(
    context,
    R.layout.task_card,
    taskList
) {
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val task = taskList[position]

        var taskCardView = convertView
        if (taskCardView == null) {
            TaskCardBinding.inflate(
                context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                parent,
                false
            ).apply {
                val taskCardViewHolder = TaskCardViewHolder(taskTitle, taskDescription, taskDueDate)
                taskCardView = root
                (taskCardView as CardView).tag = taskCardViewHolder
            }
        }

        val viewHolder = taskCardView?.tag as TaskCardViewHolder
        viewHolder.titleTv.text = task.title
        viewHolder.descriptionTv.text = task.description
        viewHolder.dueDateTv.text = formatDate(task.dueDate)

        return taskCardView as View
    }

    private fun formatDate(date: Date?): String {
        return date?.let { dateFormatter.format(it) } ?: ""
    }

    private data class TaskCardViewHolder(val titleTv: TextView, val descriptionTv: TextView, val dueDateTv: TextView)
}