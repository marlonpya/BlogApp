package com.app.blogapp.presentation.notelist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.blogapp.R
import com.app.blogapp.domain.usecase.GetNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(NoteListContract.State())
    val state: StateFlow<NoteListContract.State> = _state.asStateFlow()

    private val _effect = Channel<NoteListContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        getNotesUseCase()
            .onEach { notes ->
                _state.update { it.copy(notes = notes, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: NoteListContract.Intent) {
        when (intent) {
            is NoteListContract.Intent.CreateNoteClicked -> {
                viewModelScope.launch {
                    _effect.send(NoteListContract.Effect.NavigateToEditor)
                }
            }

            is NoteListContract.Intent.EditNoteClicked -> {
                // TODO(Ejercicio 2): navegar al editor con noteId y precargar la nota.
                viewModelScope.launch {
                    _effect.send(
                        NoteListContract.Effect.ShowMessage(
                            context.getString(R.string.feature_not_implemented)
                        )
                    )
                }
            }

            is NoteListContract.Intent.DeleteNoteClicked -> {
                // TODO(Ejercicio 1): invocar DeleteNoteUseCase(intent.noteId) en viewModelScope.
                viewModelScope.launch {
                    _effect.send(
                        NoteListContract.Effect.ShowMessage(
                            context.getString(R.string.feature_not_implemented)
                        )
                    )
                }
            }
        }
    }
}
