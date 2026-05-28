package com.puredraft.notes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isLocked: Boolean,
    val isPinned: Boolean,
    val noteColor: Int,
    val formattingData: String // Storing serialized formatting info or HTML
)
