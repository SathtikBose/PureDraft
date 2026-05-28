package com.puredraft.notes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puredraft.notes.data.local.entity.NoteEntity
import com.puredraft.notes.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val notes: StateFlow<List<NoteEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllNotes()
            } else {
                repository.searchNotes(query)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Simulate a small delay to show the skeleton loader
        viewModelScope.launch {
            delay(1500)
            _isLoading.value = false
            
            // Mock Data For UI Verification
            if (repository.getNoteById(1) == null) {
                repository.insertNote(
                    NoteEntity(
                        title = "Welcome to PureDraft",
                        content = "This is a premium glassmorphism notes app.",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis(),
                        isLocked = false,
                        isPinned = true,
                        noteColor = 0xFF1E1E1E.toInt(),
                        formattingData = ""
                    )
                )
                repository.insertNote(
                    NoteEntity(
                        title = "Secret Notes",
                        content = "You shouldn't see this because it's locked.",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis(),
                        isLocked = true,
                        isPinned = false,
                        noteColor = 0xFF1E1E1E.toInt(),
                        formattingData = ""
                    )
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
