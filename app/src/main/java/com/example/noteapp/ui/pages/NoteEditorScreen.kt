package com.example.noteapp.ui.pages

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.noteapp.store.Note
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(context: Context, note: Note, isNewNote:Boolean, onSave: (savedNote: Note) -> Unit) {
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }
    var notificationTime by remember { mutableStateOf(note.notificationTime) }

    val notificationTimeAsDate = remember(notificationTime) {
        if (notificationTime != null)
        Calendar.getInstance().apply { timeInMillis = notificationTime as Long }.time.toString()
        else "Напоминание отсутствует"
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    if (isNewNote) "Новая заметка" else "Изменить заметку",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val savedNote = Note(id = note.id, title = title, content = content, notificationTime = notificationTime)

                onSave(savedNote)
            }) {
                Text(text = "✔", style = MaterialTheme.typography.bodyLarge)
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(64.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val initialTime = notificationTime ?.let {
                    Calendar.getInstance().apply { timeInMillis = it }
                }

                showDateTimePicker(context, initialTime) { selectedTime ->
                    notificationTime = selectedTime
                }
            }) {
                Text("Поставить напоминание")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notificationTimeAsDate,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun showDateTimePicker(
    context: Context,
    initialTime: Calendar? = null,
    onDateTimeSelected: (Long) -> Unit
) {
    val currentTime = initialTime ?: Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, day ->
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    val selectedTime = Calendar.getInstance().apply {
                        set(year, month, day, hour, minute, 0)
                    }.timeInMillis
                    onDateTimeSelected(selectedTime)
                },
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                true
            ).show()
        },
        currentTime.get(Calendar.YEAR),
        currentTime.get(Calendar.MONTH),
        currentTime.get(Calendar.DAY_OF_MONTH)
    ).show()
}