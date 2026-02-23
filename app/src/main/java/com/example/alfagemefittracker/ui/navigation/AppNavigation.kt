
package com.example.alfagemefittracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alfagemefittracker.ui.screens.AddWorkoutScreen
import com.example.alfagemefittracker.ui.screens.ExerciseDetailScreen
import com.example.alfagemefittracker.ui.screens.WorkoutDetailScreen
import com.example.alfagemefittracker.ui.screens.WorkoutListScreen
import com.example.alfagemefittracker.ui.viewmodel.WorkoutViewModel

object AppDestinations {
    const val WORKOUT_LIST = "workout_list"
    const val WORKOUT_DETAIL = "workout_detail"
    const val EXERCISE_DETAIL = "exercise_detail"
    const val ADD_WORKOUT = "add_workout"
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
                onNavigateToAddWorkout = { navController.navigate(AppDestinations.ADD_WORKOUT) }
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
