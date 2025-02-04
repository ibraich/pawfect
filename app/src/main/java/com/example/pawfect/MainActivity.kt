package com.example.pawfect

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.Manifest
import androidx.navigation.navDeepLink

val personalityScoresCache = mutableMapOf<String, Map<String, Double>>()


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Question.loadQuestions(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }

        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val auth = Firebase.auth
    val startDestination = if (auth.currentUser != null) "profile_screen" else "login_screen"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("registration_first_screen") { RegistrationFirstScreen(navController) }
        composable(
            "registration_second_screen/{email}/{password}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            val password = backStackEntry.arguments?.getString("password")
            RegistrationSecondScreen(navController, email, password)
        }
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
                navArgument("friendId") { type = NavType.StringType } // Use StringType for Firestore IDs
            )
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getString("friendId")
            if (friendId != null) {
                PlanActivityScreen(navController, friendId)
            }
        }
        composable(
            route = "common_puppies_screen/{currentUserId}/{friendId}",
            arguments = listOf(
                navArgument("currentUserId") { type = NavType.StringType },
                navArgument("friendId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("currentUserId")
            val friendId = backStackEntry.arguments?.getString("friendId")

            if (currentUserId != null && friendId != null) {
                CommonPuppiesScreen(navController, currentUserId, friendId)
            }
        }

        composable(
            route = "chat_screen/{friendId}",
            arguments = listOf(navArgument("friendId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "myapp://chat/{friendId}" })
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getString("friendId")
            if (friendId != null) {
                ChatScreen(navController, friendId)
            }
        }

        composable("profile_settings_screen") {
            ProfileSettingsScreen(navController)
        }

        composable("calibrate_personality_screen") {
            PersonalityCalibrationScreen(navController)
        }

        composable("ai_calibrate_personality_screen") {
            AIPersonalityCalibrationScreen(navController)
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

