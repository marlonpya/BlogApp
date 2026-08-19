package com.app.blogapp.domain.usecase

import com.app.blogapp.domain.model.Note
import com.app.blogapp.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Obtiene una nota por id para precargarla en el editor.
 *
 * EJERCICIO 2 — Editar
 * 1. Delega en NoteRepository.getNoteById(id) (ya implementado en la capa data).
 * 2. En NoteEditorViewModel, invócalo cuando la ruta llegue con un noteId != null
 *    y usa el resultado para precargar los campos de título y contenido del State.
 * 3. Si el resultado es null (id inexistente), decide un fallback razonable,
 *    por ejemplo emitir Effect.ShowMessage y navegar atrás.
 */
class GetNoteByIdUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long): Note? {
        TODO("Ejercicio 2: implementar la carga de la nota por id")
    }
}
