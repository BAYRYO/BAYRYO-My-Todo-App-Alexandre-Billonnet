package com.example.mytodoapp.android

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mytodoapp.android.MainActivity.Companion.db
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTaskLayout : AppCompatActivity() {
    private val layout = R.layout.edit_task_layout

    private lateinit var appTitle: TextView
    private lateinit var editTextTaskTitle: TextInputLayout
    private lateinit var buttonDelete: Button
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: FloatingActionButton
    private lateinit var buttonPickDate: Button
    private lateinit var menuLayoutTaskStatus: TextInputLayout
    private lateinit var editTextTaskDeadline: TextInputLayout
    private lateinit var editTextTaskDescription: TextInputLayout
    private lateinit var errorMessage: TextView
    private lateinit var menuTaskStatus: AutoCompleteTextView

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)

        appTitle = findViewById(R.id.app_title)
        editTextTaskTitle = findViewById(R.id.editTextTaskTitle)
        buttonDelete = findViewById(R.id.buttonDeleteTask)
        buttonSave = findViewById(R.id.buttonEditTask)
        buttonCancel = findViewById(R.id.buttonCancel)
        buttonPickDate = findViewById(R.id.buttonPickDate)
        menuLayoutTaskStatus = findViewById(R.id.menuTaskStatus)
        editTextTaskDeadline = findViewById(R.id.editTextTaskDeadline)
        editTextTaskDescription = findViewById(R.id.editTextTaskDescription)
        errorMessage = findViewById(R.id.errorMessage)
        menuTaskStatus = findViewById(R.id.menuTaskStatusAutoComplete)

        GlobalScope.launch(Dispatchers.IO) {
            val taskId = intent.getIntExtra("taskId", 0)
            val task = db.taskDao().getTaskById(taskId)
            runOnUiThread {
                cancelButton()
                editButton()
                deleteButton()
                editTextTaskTitle.editText?.setText(
                    task.title
                )
                editTextTaskDescription.editText?.setText(
                    task.description
                )
                editTextTaskDeadline.editText?.setText(
                    task.deadline
                )
                val currentState = task.state
                val adapter = menuTaskStatus.adapter
                if (adapter is ArrayAdapter<*>) {
                    val position =
                        (0 until adapter.count).firstOrNull { adapter.getItem(it) == currentState }
                    if (position != null) {
                        menuTaskStatus.setText(
                            adapter.getItem(position) as? String,
                            false
                        )
                    }
                }
                showDatePicker()
            }
        }
    }

    private fun showDatePicker() {
        val deadlineText = editTextTaskDeadline.editText?.text.toString()
        val deadlineDate = parseDate(deadlineText)

        val datePicker: MaterialDatePicker<Long> = createDatePicker(deadlineDate)

        buttonPickDate.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener { selectedDate ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDate
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = sdf.format(calendar.time)
            editTextTaskDeadline.editText?.setText(formattedDate)
        }
    }


    private fun createDatePicker(initialDate: Long): MaterialDatePicker<Long> {
        val builder = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select deadline date")
            .setSelection(initialDate)

        val now = Calendar.getInstance()
        val constraintsBuilder = CalendarConstraints.Builder()
        constraintsBuilder.setStart(now.timeInMillis)
        builder.setCalendarConstraints(constraintsBuilder.build())

        return builder.build()
    }

    private fun parseDate(dateString: String): Long {
        return if (dateString.isNotEmpty()) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.parse(dateString)
            date?.time ?: Calendar.getInstance().timeInMillis
        } else {
            Calendar.getInstance().timeInMillis
        }

    }

    private fun cancelButton() {
        buttonCancel.setOnClickListener {
            finish()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun editButton(){
        val taskId = intent.getIntExtra("taskId", 0)
        buttonSave.setOnClickListener {
            val title = editTextTaskTitle.editText?.text.toString()
            val description = editTextTaskDescription.editText?.text.toString()
            val deadline = editTextTaskDeadline.editText?.text.toString()
            val state = menuLayoutTaskStatus.editText?.text.toString()
            val task = Task(taskId, title, description, deadline, state)
            if (title.isEmpty() || state.isEmpty()) {
                val error = findViewById<TextView>(R.id.errorMessage)
                error.visibility = TextView.VISIBLE
                return@setOnClickListener
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        db.taskDao().updateTask(taskId, task.title, task.deadline, task.description, task.state)
                        if (state == "Done") {
                            runOnUiThread {
                                val lottieAnimationView : com.airbnb.lottie.LottieAnimationView = findViewById(R.id.animationView)
                                lottieAnimationView.visibility = View.VISIBLE
                                Log.d("AnimationDebug", "Animation will start now")

                                editTextTaskTitle.visibility = View.GONE
                                buttonDelete.visibility = View.GONE
                                buttonCancel.visibility = View.GONE
                                buttonPickDate.visibility = View.GONE
                                appTitle.visibility = View.GONE
                                menuTaskStatus.visibility = View.GONE
                                buttonSave.visibility = View.GONE
                                editTextTaskDeadline.visibility = View.GONE
                                editTextTaskDescription.visibility = View.GONE
                                editTextTaskTitle.visibility = View.GONE
                                errorMessage.visibility = View.GONE
                                menuLayoutTaskStatus.visibility = View.GONE

                                lottieAnimationView.playAnimation()

                                lottieAnimationView.addAnimatorUpdateListener {
                                    if (it.animatedFraction == 1f) {
                                        lottieAnimationView.visibility = View.GONE
                                        finish()
                                    }
                                }
                            }
                        } else {
                            finish()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun deleteButton(){
        val taskId = intent.getIntExtra("taskId", 0)
        buttonDelete.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                db.taskDao().deleteById(taskId)
            }
            finish()
        }
    }
}