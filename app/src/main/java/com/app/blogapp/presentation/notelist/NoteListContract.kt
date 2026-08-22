package com.app.blogapp.presentation.notelist

import com.app.blogapp.domain.model.Note

/** Contrato MVI de la pantalla de listado. */
interface NoteListContract {

    /** Estado único e inmutable que la UI renderiza. Sin lógica de negocio. */
    data class State(
        val notes: List<Note> = emptyList(),
        val isLoading: Boolean = true,
        val errorMessage: String? = null
    ) {
        // Derivado: evita que la UI recalcule condiciones de vacío.
        val isEmpty: Boolean get() = !isLoading && notes.isEmpty()
    }

    /** Todo lo que el usuario puede hacer en la pantalla. */
    sealed interface Intent {
        data object CreateNoteClicked : Intent
        data class EditNoteClicked(val noteId: Long) : Intent
        data class DeleteNoteClicked(val noteId: Long) : Intent
    }

    /** Eventos de una sola vez: navegación y mensajes. Nunca viven en el State. */
    sealed interface Effect {
        data class NavigateToEditor(val noteId: Long? = null) : Effect
        data class ShowMessage(val message: String) : Effect
    }
}
