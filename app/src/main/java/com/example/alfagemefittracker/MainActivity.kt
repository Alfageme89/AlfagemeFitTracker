
package com.example.alfagemefittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.auth0.android.Auth0
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

        // INICIALIZACIÓN DE AUTH0 SEGÚN LA GUÍA OFICIAL
        val account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        val authFactory = AuthViewModelFactory(account)
        val authViewModel: AuthViewModel by viewModels { authFactory }

        val database = DatabaseProvider.getDatabase(applicationContext)
        val exerciseApiService = RetrofitClient.instance
        val workoutRepository = WorkoutRepository(database.workoutDao(), exerciseApiService)
        val workoutFactory = WorkoutViewModelFactory(workoutRepository)
        val workoutViewModel: WorkoutViewModel by viewModels { workoutFactory }

        setContent {
            AlfagemeFitTrackerTheme {
                AppNavigation(
                    authViewModel = authViewModel,
                    workoutViewModel = workoutViewModel
                )
            }
        }
    }
}
