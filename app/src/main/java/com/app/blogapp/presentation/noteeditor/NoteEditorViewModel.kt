package com.app.blogapp.presentation.noteeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.app.blogapp.domain.usecase.CreateNoteUseCase
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Hoy siempre llega null (modo creación). Se lee ya para que el Ejercicio 2
    // solo tenga que precargar el estado, sin tocar la navegación.
    private val noteId: Long? = savedStateHandle.toRoute<Routes.NoteEditorRoute>().noteId

    private val _state = MutableStateFlow(NoteEditorContract.State())
    val state: StateFlow<NoteEditorContract.State> = _state.asStateFlow()

    private val _effect = Channel<NoteEditorContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: NoteEditorContract.Intent) {
        when (intent) {
            is NoteEditorContract.Intent.TitleChanged -> {
                _state.update {
                    it.copy(title = intent.title, isSaveEnabled = intent.title.isNotBlank())
                }
            }

            is NoteEditorContract.Intent.ContentChanged -> {
                _state.update { it.copy(content = intent.content) }
            }

            is NoteEditorContract.Intent.SaveClicked -> {
                viewModelScope.launch {
                    val current = _state.value
                    createNoteUseCase(title = current.title, content = current.content)
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
