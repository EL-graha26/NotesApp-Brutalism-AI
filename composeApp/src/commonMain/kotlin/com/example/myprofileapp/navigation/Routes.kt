package com.example.myprofileapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Notes : Screen("notes")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
    object AddNote : Screen("add_note")
    object AiAssistant : Screen("ai_assistant")

    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Int) = "note_detail/$noteId"
    }

    object EditNote : Screen("edit_note/{noteId}") {
        fun createRoute(noteId: Int) = "edit_note/$noteId"
    }
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Notes : BottomNavItem(Screen.Notes.route, Icons.Default.Home, "Notes")
    object Favorites : BottomNavItem(Screen.Favorites.route, Icons.Default.Favorite, "Favorites")
    object Profile : BottomNavItem(Screen.Profile.route, Icons.Default.Person, "Profile")
}