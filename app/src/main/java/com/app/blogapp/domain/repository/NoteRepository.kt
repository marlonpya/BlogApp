package com.app.blogapp.domain.repository

import com.app.blogapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acceso a datos de notas. La implementación real vive en data,
 * domain solo conoce esta interfaz.
 */
interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun createNote(title: String, content: String)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: Long)
}
