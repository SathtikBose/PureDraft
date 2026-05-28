package com.puredraft.notes.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Home : Screen("home_screen")
    object Editor : Screen("editor_screen/{noteId}") {
        fun createRoute(noteId: Long) = "editor_screen/$noteId"
    }
}
