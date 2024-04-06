package com.example.mytodoapp.android

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class TaskAdapter(context: Context, private var tasks: List<Task>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return tasks.size
    }

    override fun getItem(position: Int): Any {
        return tasks[position]
    }

    override fun getItemId(position: Int): Long {
        return tasks[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.task_layout, parent, false)
        }

        if (convertView != null) {
            convertView.id = tasks[position].id
        }

        val textViewTitle: TextView = convertView!!.findViewById(R.id.taskTitle)
        textViewTitle.text = tasks[position].title

        val textViewDescription: TextView = convertView.findViewById(R.id.textViewTaskDescription)
        textViewDescription.text = tasks[position].description

        val textViewDeadline: TextView =
            convertView.findViewById(R.id.textViewTaskDeadline)
        textViewDeadline.text = tasks[position].deadline

        val textViewStatus: TextView = convertView.findViewById(R.id.textViewTaskStatus)
        textViewStatus.text = tasks[position].state

        val buttonEditTask = convertView.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.buttonEditTask)
        buttonEditTask.setOnClickListener {
            val intent = Intent(convertView.context, EditTaskLayout::class.java)
            intent.putExtra("taskId", tasks[position].id)
            convertView.context.startActivity(intent)
        }
        return convertView
    }

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}
