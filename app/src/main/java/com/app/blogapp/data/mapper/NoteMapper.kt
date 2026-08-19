package com.app.blogapp.data.mapper

import com.app.blogapp.data.local.entity.NoteEntity
import com.app.blogapp.domain.model.Note

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt
)
