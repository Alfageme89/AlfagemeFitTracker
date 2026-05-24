
package com.example.alfagemefittracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alfagemefittracker.ui.screens.*
import com.example.alfagemefittracker.ui.viewmodel.WorkoutViewModel

object AppDestinations {
    const val WORKOUT_LIST = "workout_list"
    const val WORKOUT_DETAIL = "workout_detail"
    const val EXERCISE_DETAIL = "exercise_detail"
    const val ADD_WORKOUT = "add_workout"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation(workoutViewModel: WorkoutViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppDestinations.WORKOUT_LIST) {
        composable(AppDestinations.WORKOUT_LIST) {
            WorkoutListScreen(
                workoutViewModel = workoutViewModel,
                onNavigateToWorkoutDetail = { workoutId ->
                    navController.navigate("${AppDestinations.WORKOUT_DETAIL}/$workoutId")
                },
                onNavigateToExerciseDetail = { exerciseId ->
                    navController.navigate("${AppDestinations.EXERCISE_DETAIL}/$exerciseId")
                },
                onNavigateToAddWorkout = { navController.navigate(AppDestinations.ADD_WORKOUT) },
                onNavigateToSettings = { navController.navigate(AppDestinations.SETTINGS) }
            )
        }
        composable(
            route = "${AppDestinations.WORKOUT_DETAIL}/{workoutId}",
            arguments = listOf(navArgument("workoutId") { type = NavType.IntType })
        ) { backStackEntry ->
            WorkoutDetailScreen(
                workoutId = backStackEntry.arguments?.getInt("workoutId") ?: 0,
                workoutViewModel = workoutViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "${AppDestinations.EXERCISE_DETAIL}/{exerciseId}",
            arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
        ) { backStackEntry ->
            ExerciseDetailScreen(
                viewModel = workoutViewModel,
                exerciseId = backStackEntry.arguments?.getString("exerciseId"),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(AppDestinations.ADD_WORKOUT) {
            AddWorkoutScreen(
                viewModel = workoutViewModel,
                onWorkoutSaved = { navController.popBackStack() }
            )
        }
        composable(AppDestinations.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = { 
                    // Aquí iría la lógica de Auth0 después
                    navController.navigate(AppDestinations.WORKOUT_LIST) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}
