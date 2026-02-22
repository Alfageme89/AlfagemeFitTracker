
package com.example.alfagemefittracker.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alfagemefittracker.ui.screens.AddWorkoutScreen
import com.example.alfagemefittracker.ui.screens.ExerciseDetailScreen
import com.example.alfagemefittracker.ui.screens.LoginScreen
import com.example.alfagemefittracker.ui.screens.WorkoutListScreen
import com.example.alfagemefittracker.ui.viewmodel.AuthViewModel
import com.example.alfagemefittracker.ui.viewmodel.WorkoutViewModel

object AppDestinations {
    const val LOGIN = "login"
    const val WORKOUT_LIST = "workout_list"
    const val EXERCISE_DETAIL = "exercise_detail"
    const val ADD_WORKOUT = "add_workout"
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    workoutViewModel: WorkoutViewModel
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val credentials by authViewModel.credentials.collectAsState()

    if (credentials == null) {
        // If not authenticated, show only the login screen
        NavHost(navController = navController, startDestination = AppDestinations.LOGIN) {
            composable(AppDestinations.LOGIN) {
                LoginScreen(onLoginClick = { authViewModel.login(context) })
            }
        }
    } else {
        // If authenticated, show the main app navigation
        NavHost(navController = navController, startDestination = AppDestinations.WORKOUT_LIST) {
            composable(AppDestinations.WORKOUT_LIST) {
                WorkoutListScreen(
                    workoutViewModel = workoutViewModel,
                    authViewModel = authViewModel,
                    onNavigateToDetail = { exerciseId ->
                        navController.navigate("${AppDestinations.EXERCISE_DETAIL}/$exerciseId")
                    },
                    onNavigateToAddWorkout = { navController.navigate(AppDestinations.ADD_WORKOUT) },
                    onLogout = { authViewModel.logout(context) }
                )
            }
            composable(
                route = "${AppDestinations.EXERCISE_DETAIL}/{exerciseId}",
                arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
            ) {
                backStackEntry ->
                ExerciseDetailScreen(
                    viewModel = workoutViewModel,
                    exerciseId = backStackEntry.arguments?.getString("exerciseId")
                )
            }
            composable(AppDestinations.ADD_WORKOUT) {
                AddWorkoutScreen(
                    viewModel = workoutViewModel,
                    onWorkoutSaved = { navController.popBackStack() }
                )
            }
        }
    }
}
