package com.example.pawfect

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Question.loadQuestions(this)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login_screen") {
        composable("login_screen") { LoginScreen(navController) }
        composable("profile_screen") { ProfileScreen(navController) }
        composable("friend_list_screen") { FriendListScreen(navController) }
        // Insert activity with map
        composable("profile_screen") { ProfileScreen(navController) }
        composable("match_screen") { MatchScreen(navController) }
        composable("recognize_breed_screen") { RecognizeBreedScreen(navController) }

        composable(
            route = "plan_activity_screen/{friendId}",
            arguments = listOf(
                navArgument("friendId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getInt("friendId")
            if (friendId != null) {
                PlanActivityScreen(navController, friendId)
            }
        }
        composable("common_puppies_screen") {
            CommonPuppiesScreen(navController)
        }

        composable(
            route = "chat_screen/{friendId}",
            arguments = listOf(
                navArgument("friendId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getInt("friendId")
            if (friendId != null) {
                ChatScreen(navController, friendId = friendId)
            }
        }

        composable("profile_settings_screen") {
            ProfileSettingsScreen(navController)
        }

        composable("calibration_screen") {
            CalibrationScreen(navController)
        }

        composable("ai_calibration_screen") {
            AICalibrationScreen(navController)
        }

        composable("routes_screen") {
            UserRoutesScreen(navController)
        }

        composable("walk_path_screen/{coordinates}") { backStackEntry ->
            val coordinates = backStackEntry.arguments?.getString("coordinates")
            WalkPathScreen(navController, coordinates)
        }


        composable("gemini_wait_screen") {
            GeminiWaitScreen(navController)
        }
    }
}

