
package com.example.alfagemefittracker.data.remote

import com.example.alfagemefittracker.data.remote.dto.ExerciseDto
import retrofit2.http.GET

interface ExerciseApiService {

    @GET("exercises")
    suspend fun getExercises(): List<ExerciseDto>
}
