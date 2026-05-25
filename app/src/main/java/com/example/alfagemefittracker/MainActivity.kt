package com.example.alfagemefittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.alfagemefittracker.data.local.DatabaseProvider
import com.example.alfagemefittracker.data.remote.RetrofitClient
import com.example.alfagemefittracker.data.repository.WorkoutRepository
import com.example.alfagemefittracker.ui.navigation.AppNavigation
import com.example.alfagemefittracker.ui.theme.AlfagemeFitTrackerTheme
import com.example.alfagemefittracker.ui.viewmodel.AuthViewModel
import com.example.alfagemefittracker.ui.viewmodel.AuthViewModelFactory
import com.example.alfagemefittracker.ui.viewmodel.WorkoutViewModel
import com.example.alfagemefittracker.ui.viewmodel.WorkoutViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = DatabaseProvider.getDatabase(applicationContext)
        val exerciseApiService = RetrofitClient.instance
        val workoutRepository = WorkoutRepository(database.workoutDao(), database.workoutLogDao(), exerciseApiService)
        
        val workoutFactory = WorkoutViewModelFactory(workoutRepository)
        val workoutViewModel: WorkoutViewModel by viewModels { workoutFactory }
        
        val authFactory = AuthViewModelFactory(this)
        val authViewModel: AuthViewModel by viewModels { authFactory }

        setContent {
            AlfagemeFitTrackerTheme {
                AppNavigation(
                    workoutViewModel = workoutViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
