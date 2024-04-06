package com.example.mytodoapp.android

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import android.Manifest
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var db: AppDatabase
    }

    private val postNotificationsRequestCode = 100
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        searchTasks()

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "myapp-db"
        ).build()

        val listView: ListView = findViewById(R.id.taskLayout)
        adapter = TaskAdapter(this, emptyList())
        listView.adapter = adapter

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddTaskLayout::class.java)
            startActivity(intent)
        }

        updateListView()

        val filtersButton = findViewById<FloatingActionButton>(R.id.filtersButton)
        filtersButton.setOnClickListener { v: View ->
            showMenu(v, R.menu.filters_menu)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                postNotificationsRequestCode
            )
        } else {
            onRequestPermissionsResult(postNotificationsRequestCode, arrayOf(Manifest.permission.POST_NOTIFICATIONS), intArrayOf(PackageManager.PERMISSION_GRANTED))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }

    override fun onResume() {
        super.onResume()
        updateListView()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateListView() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val tasks = db.taskDao().getAll()
                tasks.forEach {
                    if (it.state == "To Do") {
                        if (it.deadline < SimpleDateFormat("dd/MM/yyyy").format(Date())) {
                            db.taskDao().updateState(it.id, "Late")
                        }
                    }
                }
                launch(Dispatchers.Main) {
                    adapter.updateTasks(tasks)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showMenu(v: View, @MenuRes filtersMenu: Int) {
        val popup = PopupMenu(ContextThemeWrapper(this, R.style.Widget_Material3_PopupMenu_MyCustomStyle), v)
        popup.menuInflater.inflate(filtersMenu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val tasks = when (menuItem.itemId) {
                        R.id.all_tasks -> db.taskDao().getAll()
                        else -> db.taskDao().getTasksByState(menuItem.title.toString())
                    }
                    launch(Dispatchers.Main) {
                        adapter.updateTasks(tasks)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            true
        }
        popup.show()
    }

    private fun startNotificationWorker() {
        val constraints = Constraints.Builder()
            .build()
        val request = PeriodicWorkRequest.Builder(
            NotificationWorker::class.java,
            30,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueue(request)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == postNotificationsRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startNotificationWorker()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun searchTasks() {
        val searchEditText = findViewById<EditText>(R.id.search)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }

            override fun afterTextChanged(s: Editable) {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val tasks = db.taskDao().searchTasksByTitle(s.toString())
                        launch(Dispatchers.Main) {
                            adapter.updateTasks(tasks)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        )
    }
}