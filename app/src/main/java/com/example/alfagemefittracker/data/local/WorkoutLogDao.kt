package com.example.alfagemefittracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutLogDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkoutLog(workoutLog: WorkoutLog)

    @Query("SELECT * FROM workout_logs WHERE workoutId = :workoutId ORDER BY id ASC")
    fun getLogsForWorkout(workoutId: Int): Flow<List<WorkoutLog>>

    @Update
    suspend fun updateWorkoutLog(workoutLog: WorkoutLog)

}
