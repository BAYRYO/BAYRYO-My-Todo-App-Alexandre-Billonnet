package com.example.mytodoapp.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.mytodoapp.android.MainActivity.Companion.db
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import android.Manifest
import android.content.pm.PackageManager

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val task = getTasksNeedingNotification()
        return if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Result.failure()
        } else {
            task.forEach {
                if (it.state == "Late") {
                    sendNotification(it, "Task ${it.title} is overdue!")
                } else if (it.state == "To Do") {
                    sendNotification(it, "Task ${it.title} is due soon!")
                }
            }
            Result.success()
        }
    }

    private fun getTasksNeedingNotification(): List<Task> {
        val tasks = db.taskDao().getAll()

        val currentTime = System.currentTimeMillis()

        return tasks.filter { task ->
            val deadlineDate = SimpleDateFormat("dd/MM/yyyy").parse(task.deadline)
            val deadlineTime = deadlineDate?.time
            val timeDifference = deadlineTime?.minus(currentTime)

            deadlineTime!! < currentTime || timeDifference!! <= TimeUnit.HOURS.toMillis(24)
        }
    }



    private fun sendNotification(task: Task, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "TaskNotifications"
        val channel = NotificationChannel(channelId,"Task Notifications",NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Task Reminder")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification_24)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(task.id, builder.build())
    }


}