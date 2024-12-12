package com.example.noteapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteapp.store.Note
import com.example.noteapp.ui.components.NoteCard
import kotlinx.coroutines.launch

@Composable
fun NoteListScreen(notes: List<Note>, onDeleteNote: (noteId: Int) -> Unit, navigateToNoteForm: (Int?) -> Unit) {
    val snackbarRef = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navigateToNoteForm(null) }) {
                Text(text = "+", style = MaterialTheme.typography.bodyLarge)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarRef) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (notes.isEmpty()) {
                item {
                    Text(
                        text = "Заметок нет",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(notes.size) { index ->
                    val note = notes[index]
                    NoteCard(
                        note,
                        onDeleteNote = { noteId: Int ->
                            coroutineScope.launch {
                                onDeleteNote(noteId)
                                snackbarRef.showSnackbar("Удалено")
                            }
                        },
                        cardClick = { noteId: Int ->
                            navigateToNoteForm(noteId)
                        })
                }
            }
        }
    }
}
