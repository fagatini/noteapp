package com.example.noteapp.notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.noteapp.store.Note
import java.util.Calendar

//Устанавливает напоминание с помощью AlarmManager
@SuppressLint("ScheduleExactAlarm")
fun setNotification(context: Context, note: Note) {
    if (note.notificationTime != null) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timeInString = Calendar.getInstance().apply { timeInMillis = note.notificationTime }.time.toString()

        // Создаем Intent для напоминания
        val intent = Intent(context, Notificator::class.java).apply {
            putExtra("noteId", note.id)
            putExtra("title", note.title)
            putExtra("content", note.content)
        }

        // Создаем PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            note.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Устанавливаем напоминание
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            note.notificationTime,
            pendingIntent
        )

        Toast.makeText(context, "Напоминание установлено на $timeInString", Toast.LENGTH_SHORT).show()
    }
}
