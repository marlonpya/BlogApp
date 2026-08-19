package com.app.blogapp.domain.usecase

import com.app.blogapp.domain.model.Note
import com.app.blogapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Expone las notas ordenadas por última edición.
 * Devuelve Flow para que la UI reaccione a los cambios de Room sin recargar.
 */
class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> = repository.observeNotes()
}
