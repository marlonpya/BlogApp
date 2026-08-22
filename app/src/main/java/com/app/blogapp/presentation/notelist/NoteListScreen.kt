package com.app.blogapp.presentation.notelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.blogapp.R
import com.app.blogapp.domain.model.Note
import com.app.blogapp.presentation.theme.NotesTheme
import kotlinx.coroutines.launch

@Composable
fun NoteListRoute(
    onNavigateToEditor: () -> Unit,
    viewModel: NoteListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NoteListContract.Effect.NavigateToEditor -> onNavigateToEditor()
                is NoteListContract.Effect.ShowMessage -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }
    }

    NoteListScreen(
        state = state,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    state: NoteListContract.State,
    onIntent: (NoteListContract.Intent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.note_list_title)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onIntent(NoteListContract.Intent.CreateNoteClicked) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.note_list_add_content_description)
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.isEmpty -> {
                    NoteListEmptyContent(modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.notes, key = { it.id }) { note ->
                            NoteItem(
                                note = note,
                                onEditClicked = { id ->
                                    onIntent(NoteListContract.Intent.EditNoteClicked(id))
                                },
                                onDeleteClicked = { id ->
                                    onIntent(NoteListContract.Intent.DeleteNoteClicked(id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    state.notePendingDeletion?.let { note ->
        DeleteNoteConfirmationDialog(
            note = note,
            onConfirm = { onIntent(NoteListContract.Intent.ConfirmDeleteClicked) },
            onDismiss = { onIntent(NoteListContract.Intent.DismissDeleteClicked) }
        )
    }
}

@Composable
private fun DeleteNoteConfirmationDialog(
    note: Note,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.note_delete_dialog_title)) },
        text = {
            Text(
                stringResource(
                    R.string.note_delete_dialog_message,
                    note.title
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.note_delete_confirm_action))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.note_delete_cancel_action))
            }
        }
    )
}

@Composable
private fun NoteListEmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Notes,
            contentDescription = null,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = stringResource(R.string.note_list_empty_message),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteListScreenPreview() {
    NotesTheme {
        NoteListScreen(
            state = NoteListContract.State(
                notes = listOf(
                    Note(1L, "Compras", "Leche, huevos, pan", 0L, 0L),
                    Note(2L, "Ideas", "Prototipo de la app de notas", 0L, 0L)
                ),
                isLoading = false
            ),
            onIntent = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteListScreenEmptyPreview() {
    NotesTheme {
        NoteListScreen(
            state = NoteListContract.State(notes = emptyList(), isLoading = false),
            onIntent = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
