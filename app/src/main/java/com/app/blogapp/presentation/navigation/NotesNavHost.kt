package com.app.blogapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.blogapp.presentation.noteeditor.NoteEditorRoute
import com.app.blogapp.presentation.notelist.NoteListRoute

@Composable
fun NotesNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.NoteListRoute
    ) {
        composable<Routes.NoteListRoute> {
            NoteListRoute(
                onNavigateToEditor = { noteId ->
                    navController.navigate(Routes.NoteEditorRoute(noteId))
                }
            )
        }
        composable<Routes.NoteEditorRoute> {
            NoteEditorRoute(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
