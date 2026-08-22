package com.app.blogapp.presentation.noteeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.app.blogapp.domain.model.Note
import com.app.blogapp.domain.usecase.CreateNoteUseCase
import com.app.blogapp.domain.usecase.GetNoteByIdUseCase
import com.app.blogapp.domain.usecase.UpdateNoteUseCase
import com.app.blogapp.presentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val createNoteUseCase: CreateNoteUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val noteId: Long? = savedStateHandle.toRoute<Routes.NoteEditorRoute>().noteId
    private var originalNote: Note? = null

    private val _state = MutableStateFlow(
        NoteEditorContract.State(isLoading = noteId != null, isEditing = noteId != null)
    )
    val state: StateFlow<NoteEditorContract.State> = _state.asStateFlow()

    private val _effect = Channel<NoteEditorContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        noteId?.let(::loadNote)
    }

    private fun loadNote(id: Long) {
        viewModelScope.launch {
            val note = getNoteByIdUseCase(id)
            if (note == null) {
                _effect.send(NoteEditorContract.Effect.ShowMessage("La nota ya no existe"))
                _effect.send(NoteEditorContract.Effect.NavigateBack)
                return@launch
            }

            originalNote = note
            _state.value = NoteEditorContract.State(
                title = note.title,
                content = note.content,
                isSaveEnabled = note.title.isNotBlank(),
                isEditing = true
            )
        }
    }

    fun onIntent(intent: NoteEditorContract.Intent) {
        when (intent) {
            is NoteEditorContract.Intent.TitleChanged -> {
                _state.update {
                    it.copy(
                        title = intent.title,
                        isSaveEnabled = intent.title.isNotBlank() && !it.isLoading && !it.isSaving
                    )
                }
            }

            is NoteEditorContract.Intent.ContentChanged -> {
                _state.update { it.copy(content = intent.content) }
            }

            is NoteEditorContract.Intent.SaveClicked -> {
                viewModelScope.launch {
                    val current = _state.value
                    if (!current.isSaveEnabled) return@launch

                    val noteToUpdate = if (noteId != null) originalNote else null
                    if (noteId != null && noteToUpdate == null) {
                        _effect.send(NoteEditorContract.Effect.ShowMessage("No se pudo cargar la nota"))
                        return@launch
                    }

                    _state.update { it.copy(isSaveEnabled = false, isSaving = true) }
                    if (noteId == null) {
                        createNoteUseCase(title = current.title, content = current.content)
                    } else {
                        updateNoteUseCase(
                            noteToUpdate!!.copy(title = current.title, content = current.content)
                        )
                    }
                    _effect.send(NoteEditorContract.Effect.NavigateBack)
                }
            }

            is NoteEditorContract.Intent.BackClicked -> {
                viewModelScope.launch {
                    _effect.send(NoteEditorContract.Effect.NavigateBack)
                }
            }
        }
    }
}
