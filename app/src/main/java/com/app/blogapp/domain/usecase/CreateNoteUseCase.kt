package com.app.blogapp.domain.usecase

import com.app.blogapp.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Crea una nota nueva. Valida que el título no esté vacío antes de persistir,
 * así la regla de negocio no depende de que la UI la respete.
 */
class CreateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(title: String, content: String) {
        require(title.isNotBlank()) { "El título no puede estar vacío" }
        repository.createNote(title = title.trim(), content = content.trim())
    }
}
