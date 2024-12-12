package com.example.noteapp.store

import java.io.Serializable

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val notificationTime: Long? = null
) : Serializable