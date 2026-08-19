package com.app.blogapp.presentation.notelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.blogapp.R
import com.app.blogapp.domain.model.Note
import com.app.blogapp.presentation.theme.NotesTheme
import java.text.DateFormat
import java.util.Date

@Composable
fun NoteItem(
    note: Note,
    onEditClicked: (Long) -> Unit,
    onDeleteClicked: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = note.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(Date(note.updatedAt)),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onEditClicked(note.id) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.note_item_edit_content_description)
                    )
                }
                IconButton(onClick = { onDeleteClicked(note.id) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.note_item_delete_content_description)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteItemPreview() {
    NotesTheme {
        NoteItem(
            note = Note(
                id = 1L,
                title = "Lista de compras",
                content = "Leche, huevos, pan y café para la semana.",
                createdAt = 0L,
                updatedAt = 0L
            ),
            onEditClicked = {},
            onDeleteClicked = {}
        )
    }
}
