package com.example.noteapp.store

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NotesStorage(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("notes", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getNotesFromPreferences(): List<Note> {
        val json = sharedPreferences.getString("notes_list", null) ?: return emptyList()
        val type = object : TypeToken<List<Note>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveNotesToPreferences(notes: List<Note>) {
        val json = gson.toJson(notes)
        sharedPreferences.edit().putString("notes_list", json).apply()
    }
}