package com.app.blogapp.domain.usecase

import com.app.blogapp.domain.model.Note
import com.app.blogapp.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Actualiza una nota existente.
 *
 * EJERCICIO 2 — Editar
 * 1. Delega en NoteRepository.updateNote(note) (ya implementado en la capa data).
 * 2. Antes de guardar, refresca el campo updatedAt de la nota (System.currentTimeMillis()).
 * 3. En NoteEditorViewModel, maneja el guardado cuando la pantalla está en modo edición
 *    (noteId != null) invocando este caso de uso dentro de viewModelScope.
 * 4. No recargues la lista a mano: el Flow del DAO re-emite solo.
 */
class UpdateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        TODO("Ejercicio 2: implementar la actualización de la nota")
    }
}
