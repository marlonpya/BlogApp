package com.app.blogapp.domain.usecase

import com.app.blogapp.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Elimina una nota por id.
 *
 * EJERCICIO 1 — Eliminar
 * 1. Delega en NoteRepository.deleteNote(id) (ya implementado en la capa data).
 * 2. En NoteListViewModel, maneja Intent.DeleteNoteClicked invocando este caso de uso
 *    dentro de viewModelScope.
 * 3. No recargues la lista a mano: el Flow del DAO re-emite solo.
 * 4. Opcional: pide confirmación con un AlertDialog antes de borrar.
 */
class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long) {
        TODO("Ejercicio 1: implementar la eliminación de la nota")
    }
}
