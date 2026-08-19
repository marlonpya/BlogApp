package com.app.blogapp.data.repository

import com.app.blogapp.data.local.dao.NoteDao
import com.app.blogapp.data.local.entity.NoteEntity
import com.app.blogapp.data.mapper.toDomain
import com.app.blogapp.data.mapper.toEntity
import com.app.blogapp.domain.model.Note
import com.app.blogapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun observeNotes(): Flow<List<Note>> =
        noteDao.observeNotes().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getNoteById(id: Long): Note? =
        noteDao.getNoteById(id)?.toDomain()

    override suspend fun createNote(title: String, content: String) {
        // Sella las marcas de tiempo aquí: el caso de uso solo valida el título.
        val now = System.currentTimeMillis()
        noteDao.insert(
            NoteEntity(title = title, content = content, createdAt = now, updatedAt = now)
        )
    }

    override suspend fun updateNote(note: Note) {
        noteDao.update(note.toEntity())
    }

    override suspend fun deleteNote(id: Long) {
        noteDao.deleteById(id)
    }
}
