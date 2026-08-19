package com.app.blogapp.presentation.noteeditor

/** Contrato MVI de la pantalla de edición/creación de notas. */
interface NoteEditorContract {

    /** Estado único e inmutable que la UI renderiza. Sin lógica de negocio. */
    data class State(
        val title: String = "",
        val content: String = "",
        val isSaveEnabled: Boolean = false
    )

    /** Todo lo que el usuario puede hacer en la pantalla. */
    sealed interface Intent {
        data class TitleChanged(val title: String) : Intent
        data class ContentChanged(val content: String) : Intent
        data object SaveClicked : Intent
        data object BackClicked : Intent
    }

    /** Eventos de una sola vez: navegación y mensajes. Nunca viven en el State. */
    sealed interface Effect {
        data object NavigateBack : Effect
        data class ShowMessage(val message: String) : Effect
    }
}
