package com.app.blogapp.presentation.navigation

import kotlinx.serialization.Serializable

object Routes {
    @Serializable
    data object NoteListRoute

    @Serializable
    data class NoteEditorRoute(val noteId: Long? = null)
}
