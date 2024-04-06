package com.example.mytodoapp.android

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTaskLayout : AppCompatActivity() {
    private val layout = R.layout.add_task_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        showDatePicker()
        cancelButton()
        addButton()
    }

    private fun createDatePicker(): MaterialDatePicker<Long> {
        val builder = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select deadline date")
            .setSelection(System.currentTimeMillis())

        val now = Calendar.getInstance()
        val constraintsBuilder = CalendarConstraints.Builder()
        constraintsBuilder.setStart(now.timeInMillis)
        builder.setCalendarConstraints(constraintsBuilder.build())

        return builder.build()
    }

    private fun showDatePicker() {
        val deadlineTextView = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.editTextTaskDeadline)
        val buttonPickDate: Button = findViewById(R.id.buttonPickDate)
        val datePicker: MaterialDatePicker<Long> = createDatePicker()

        buttonPickDate.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener {
                selectedDate ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDate
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = sdf.format(calendar.time)
            deadlineTextView.editText?.setText(formattedDate)
        }
    }

    private fun cancelButton() {
        val buttonCancel: com.google.android.material.floatingactionbutton.FloatingActionButton = findViewById(R.id.buttonCancel)
        buttonCancel.setOnClickListener {
            finish()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun addButton(){
        val buttonSave: Button = findViewById(R.id.buttonAddTask)
        buttonSave.setOnClickListener {
            val title = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.editTextTaskTitle).editText?.text.toString()
            val description = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.editTextTaskDescription).editText?.text.toString()
            val deadline = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.editTextTaskDeadline).editText?.text.toString()
            val state = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.menuTaskStatus).editText?.text.toString()
            val task = Task(0, title, description, deadline, state)
            if (title.isEmpty() || state.isEmpty()) {
                val error = findViewById<TextView>(R.id.errorMessage)
                error.visibility = TextView.VISIBLE
                return@setOnClickListener
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    MainActivity.db.taskDao().insert(task)
                }
                finish()
            }
        }
    }
}

