package com.app.blogapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.blogapp.data.local.dao.NoteDao
import com.app.blogapp.data.local.entity.NoteEntity

@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
