package com.puredraft.notes.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.puredraft.notes.ui.home.HomeScreen
import com.puredraft.notes.ui.editor.EditorScreen
import com.puredraft.notes.ui.splash.SplashScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(
            route = Screen.Editor.route,
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) {
            EditorScreen(navController = navController)
        }
    }
}
