package com.example.noteapp

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.noteapp.store.Note
import com.example.noteapp.store.NotesStorage
import com.example.noteapp.notifications.setNotification
import com.example.noteapp.ui.pages.NoteEditorScreen
import com.example.noteapp.ui.pages.NoteListScreen
import com.example.noteapp.ui.theme.NoteAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        // Проверяем разрешения
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestNotificationPermission()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkAndRequestExactAlarmPermission()
        }

        val noteId = intent.getIntExtra("noteId", -1)

        setContent {
            NoteAppTheme {
                NoteApp(
                    notesStorage = NotesStorage(applicationContext),
                    initialNoteId = if (noteId != -1) noteId else null
                )
            }
        }
    }

    // Обработчик результата для точных будильников
    private val scheduleExactAlarmResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Разрешение предоставлено", Toast.LENGTH_SHORT).show()
            }
        }

    // Проверка и запрос разрешения на уведомления для Android 13+.
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Разрешение уже предоставлено
            Toast.makeText(this, "Разрешение уже предоставлено", Toast.LENGTH_SHORT).show()
        } else {
            // Запрашиваем разрешение
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }

    // Проверка и запрос разрешения на точные будильники (Android 12+).
    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkAndRequestExactAlarmPermission() {
        val alarmManager = getSystemService(AlarmManager::class.java)
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            scheduleExactAlarmResultLauncher.launch(intent)
        }
    }

    // Создание канала уведомлений для напоминаний.
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "note_notifications_channel"
            val channelName = "note notifications"
            val channelDescription = "Channel for note notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun NoteApp(notesStorage: NotesStorage, initialNoteId: Int? = null) {
    val navController = rememberNavController()
    var notes by remember { mutableStateOf(notesStorage.getNotesFromPreferences()) }

    LaunchedEffect(initialNoteId) {
        initialNoteId?.let {
            navController.navigate("noteEditor/$it")
        }
    }

    NavHost(navController, startDestination = "noteList") {
        composable("noteList") {
            NoteListScreen(
                notes,
                onDeleteNote = { noteId:Int ->
                    notes = notes.filterNot { it.id == noteId }
                    notesStorage.saveNotesToPreferences(notes)
                },
                navigateToNoteForm = { noteId:Int? ->
                    if (noteId == null) {
                        navController.navigate("noteEditor/${notes.size + 1}")
                    } else {
                        navController.navigate("noteEditor/${noteId}")
                    }
                },
            )
        }
        composable("noteEditor/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toInt()
            val note = notes.find { note -> note.id == noteId } ?: Note(id = noteId?:0, title = "", content = "")

            NoteEditorScreen(
                navController.context,
                note,
                notes.size < note.id,
                onSave = { savedNote: Note ->
                    val indexOfNoteInList = notes.indexOfFirst{it.id == savedNote.id}

                    notes = if (indexOfNoteInList == -1) {
                        notes + savedNote
                    } else {
                        notes.map { note ->
                            if (note.id == savedNote.id) savedNote else note
                        }
                    }

                    if (savedNote.notificationTime != null) {
                        setNotification(navController.context, savedNote)
                    }

                    notesStorage.saveNotesToPreferences(notes)
                    navController.navigate("noteList")
                }
            )
        }
    }
}
