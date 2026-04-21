package com.example.followme02

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.followme02.model.ExerciseType
import com.example.followme02.model.Workout
import com.example.followme02.navigation.HeaderNavigation
import com.example.followme02.screen.achievements.AchievementScreen
import com.example.followme02.screen.achievements.AchievementViewModel
import com.example.followme02.screen.auth.AuthViewModel
import com.example.followme02.screen.auth.LoginScreen
import com.example.followme02.screen.auth.RegisterScreen
import com.example.followme02.screen.home.HomeScreen
import com.example.followme02.screen.journey.JourneyLogScreen
import com.example.followme02.screen.leaderboard.LeaderboardScreen
import com.example.followme02.screen.profile.ProfileScreen
import com.example.followme02.screen.social.SocialScreen
import com.example.followme02.screen.social.TeamScreen
import com.example.followme02.screen.workout.WorkoutScreen2
import com.example.followme02.viewmodel.ProfileViewModel
import com.example.followme02.viewmodel.SocialViewModel
import com.example.followme02.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FollowMeApp(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onLoadDarkMode: () -> Unit,
    onResetDarkMode: () -> Unit
) {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val achievementViewModel: AchievementViewModel = viewModel()
    val workoutViewModel: WorkoutViewModel = viewModel()
    val socialViewModel: SocialViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        when (currentRoute) {
            "login", "register" -> onResetDarkMode()
            null -> Unit
            else -> onLoadDarkMode()
        }
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("FollowMe") }
                )

                if (currentRoute != "login" && currentRoute != "register") {
                    HeaderNavigation(navController)
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(padding)
        ) {
            composable("login") {
                LoginScreen(navController, authViewModel)
            }

            composable("register") {
                RegisterScreen(navController, authViewModel)
            }

            composable("home") {
                HomeScreen(
                    navController = navController,
                    viewModel = profileViewModel
                )
            }

            composable("workout") {
                WorkoutScreen2(
                    navController = navController,
                    workout = Workout(
                        id = 0,
                        exerciseType = ExerciseType.RUN,
                        distanceKm = 0f,
                        durationMinutes = 0,
                        date = ""
                    ),
                    onCancel = {},
                    viewModel = workoutViewModel
                )
            }

            composable("social") {
                SocialScreen(
                    navController = navController,
                    viewModel = socialViewModel
                )
            }

            composable("team") {
                TeamScreen(
                    navController = navController,
                    viewModel = socialViewModel
                )
            }

            composable("achievements") {
                AchievementScreen(navController = navController)
            }

            composable("leaderboard") {
                LeaderboardScreen(navController = navController)
            }

            composable("profile") {
                ProfileScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode
                )
            }

            composable("journey_log") {
                JourneyLogScreen()
            }
        }
    }
}