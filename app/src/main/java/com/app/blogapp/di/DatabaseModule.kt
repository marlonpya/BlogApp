package com.app.blogapp.di

import android.content.Context
import androidx.room.Room
import com.app.blogapp.data.local.NotesDatabase
import com.app.blogapp.data.local.dao.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNotesDatabase(@ApplicationContext context: Context): NotesDatabase =
        Room.databaseBuilder(
            context,
            NotesDatabase::class.java,
            "notes.db"
        ).build()

    @Provides
    fun provideNoteDao(database: NotesDatabase): NoteDao = database.noteDao()
}
