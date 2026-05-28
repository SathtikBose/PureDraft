package com.puredraft.notes.domain.repository

import com.puredraft.notes.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<NoteEntity>>
    suspend fun getNoteById(id: Long): NoteEntity?
    fun searchNotes(query: String): Flow<List<NoteEntity>>
    suspend fun insertNote(note: NoteEntity): Long
    suspend fun updateNote(note: NoteEntity)
    suspend fun deleteNote(note: NoteEntity)
    suspend fun deleteNotes(noteIds: List<Long>)
}
