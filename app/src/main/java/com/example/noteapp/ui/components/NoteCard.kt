package com.example.noteapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteapp.store.Note

@Composable
fun NoteCard(note: Note, onDeleteNote: (Int) -> Unit, cardClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
        .clickable {
                cardClick(note.id)
        }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onDeleteNote(note.id)
                }
            ) {
                Text("Удалить")
            }
        }
    }
}