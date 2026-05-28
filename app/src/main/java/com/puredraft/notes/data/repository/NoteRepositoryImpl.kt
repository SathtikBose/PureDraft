package com.puredraft.notes.data.repository

import com.puredraft.notes.data.local.dao.NoteDao
import com.puredraft.notes.data.local.entity.NoteEntity
import com.puredraft.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()
    
    override suspend fun getNoteById(id: Long): NoteEntity? = noteDao.getNoteById(id)
    
    override fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotes(query)
    
    override suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)
    
    override suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    
    override suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
    
    override suspend fun deleteNotes(noteIds: List<Long>) = noteDao.deleteNotes(noteIds)
}
