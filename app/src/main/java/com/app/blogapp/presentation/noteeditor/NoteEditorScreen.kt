package com.app.blogapp.presentation.noteeditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.blogapp.R
import com.app.blogapp.presentation.theme.NotesTheme

@Composable
fun NoteEditorRoute(
    onNavigateBack: () -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NoteEditorContract.Effect.NavigateBack -> onNavigateBack()
                is NoteEditorContract.Effect.ShowMessage -> Unit
            }
        }
    }

    NoteEditorScreen(state = state, onIntent = viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    state: NoteEditorContract.State,
    onIntent: (NoteEditorContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.note_editor_title)) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(NoteEditorContract.Intent.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.note_editor_back_content_description)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { onIntent(NoteEditorContract.Intent.SaveClicked) },
                        enabled = state.isSaveEnabled
                    ) {
                        Text(stringResource(R.string.note_editor_save_action))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = { onIntent(NoteEditorContract.Intent.TitleChanged(it)) },
                label = { Text(stringResource(R.string.note_editor_title_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.content,
                onValueChange = { onIntent(NoteEditorContract.Intent.ContentChanged(it)) },
                label = { Text(stringResource(R.string.note_editor_content_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp),
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteEditorScreenPreview() {
    NotesTheme {
        NoteEditorScreen(
            state = NoteEditorContract.State(
                title = "Nueva nota",
                content = "Contenido de ejemplo",
                isSaveEnabled = true
            ),
            onIntent = {}
        )
    }
}
