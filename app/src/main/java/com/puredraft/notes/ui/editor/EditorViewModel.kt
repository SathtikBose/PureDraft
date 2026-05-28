package com.puredraft.notes.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puredraft.notes.data.local.entity.NoteEntity
import com.puredraft.notes.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditorUiState(
    val id: Long? = null,
    val title: String = "",
    val content: String = "",
    val isPinned: Boolean = false,
    val isLocked: Boolean = false,
    val noteColor: Int = 0xFF1E1E1E.toInt(),
    val isLoading: Boolean = false
)

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val repository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val noteId: Long? = savedStateHandle.get<String>("noteId")?.toLongOrNull()

    private val _uiState = MutableStateFlow(EditorUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private var saveJob: Job? = null
    private var isInitialized = false

    init {
        loadNote()
    }

    private fun loadNote() {
        if (noteId != null && noteId > 0) {
            viewModelScope.launch {
                val note = repository.getNoteById(noteId)
                if (note != null) {
                    _uiState.update {
                        it.copy(
                            id = note.id,
                            title = note.title,
                            content = note.content,
                            isPinned = note.isPinned,
                            isLocked = note.isLocked,
                            noteColor = note.noteColor,
                            isLoading = false
                        )
                    }
                    isInitialized = true
                }
            }
        } else {
            _uiState.update { it.copy(isLoading = false) }
            isInitialized = true
        }
    }

    fun onTitleChanged(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
        scheduleSave()
    }

    fun onContentChanged(newContent: String) {
        _uiState.update { it.copy(content = newContent) }
        scheduleSave()
    }

    fun togglePin() {
        _uiState.update { it.copy(isPinned = !it.isPinned) }
        scheduleSave()
    }

    fun toggleLock() {
        _uiState.update { it.copy(isLocked = !it.isLocked) }
        scheduleSave()
    }

    fun changeColor(color: Int) {
        _uiState.update { it.copy(noteColor = color) }
        scheduleSave()
    }

    private fun scheduleSave() {
        if (!isInitialized) return
        val state = _uiState.value
        // Only save if there's actually content or title
        if (state.title.isBlank() && state.content.isBlank()) return

        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(1000) // Debounce auto-save
            saveNoteInstantly()
        }
    }

    fun saveNoteInstantly() {
        val state = _uiState.value
        if (state.title.isBlank() && state.content.isBlank()) return

        viewModelScope.launch {
            val entity = NoteEntity(
                id = state.id ?: 0L,
                title = state.title,
                content = state.content,
                createdAt = System.currentTimeMillis(), // In a real app, preserve old createdAt
                updatedAt = System.currentTimeMillis(),
                isPinned = state.isPinned,
                isLocked = state.isLocked,
                noteColor = state.noteColor,
                formattingData = ""
            )

            if (state.id == null || state.id == 0L) {
                val newId = repository.insertNote(entity)
                _uiState.update { it.copy(id = newId) }
            } else {
                repository.updateNote(entity)
            }
        }
    }
}
