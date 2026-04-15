package com.example.followme02.data.repository

import android.util.Log
import com.example.followme02.data.remote.SupabaseProvider
import com.example.followme02.model.ExerciseType
import com.example.followme02.model.TrainingSessions
import com.example.followme02.model.Workout
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserIdRow(
    @SerialName("user_id")
    val userId: Int
)

@Serializable
data class TrainingTypeRow(
    @SerialName("type_id")
    val typeId: Int,

    @SerialName("type_name")
    val typeName: String
)

@Serializable
data class TrainingSessionInsert(
    @SerialName("user_id")
    val userId: Int,

    @SerialName("type_id")
    val typeId: Int,

    @SerialName("distance_km")
    val distanceKm: Double,

    @SerialName("duration_minutes")
    val durationMinutes: Int,

    @SerialName("training_date")
    val trainingDate: String
)

class WorkoutRepository {

    private val supabase = SupabaseProvider.client

    private fun exerciseTypeToDbName(type: ExerciseType): String {
        return when (type) {
            ExerciseType.WALK -> "Walking"
            ExerciseType.RUN -> "Running"
            ExerciseType.CYCLE -> "Cycling"
            ExerciseType.SKI -> "Skiing"
        }
    }

    private fun dbNameToExerciseType(typeName: String): ExerciseType {
        return when (typeName.trim().lowercase()) {
            "walking" -> ExerciseType.WALK
            "jogging" -> ExerciseType.RUN
            "running" -> ExerciseType.RUN
            "cycling" -> ExerciseType.CYCLE
            "skiing" -> ExerciseType.SKI
            else -> ExerciseType.RUN
        }
    }

    suspend fun getCurrentDbUserId(): Int? {
        return try {
            val authUserId = supabase.auth.currentUserOrNull()?.id
            Log.d("WORKOUT_REPOSITORY", "authUserId = $authUserId")

            if (authUserId == null) {
                Log.e("WORKOUT_REPOSITORY", "Ingen innlogget auth-bruker")
                return null
            }

            val userRow = supabase
                .from("users")
                .select {
                    filter {
                        eq("auth_id", authUserId)
                    }
                }
                .decodeSingle<UserIdRow>()

            Log.d("WORKOUT_REPOSITORY", "dbUserId = ${userRow.userId}")
            userRow.userId
        } catch (e: Exception) {
            Log.e("WORKOUT_REPOSITORY", "Feil ved henting av user_id: ${e.message}", e)
            null
        }
    }

    suspend fun getTrainingTypeId(exerciseType: ExerciseType): Int? {
        return try {
            val dbTypeName = exerciseTypeToDbName(exerciseType)

            val typeRow = supabase
                .from("training_types")
                .select {
                    filter {
                        eq("type_name", dbTypeName)
                    }
                }
                .decodeSingle<TrainingTypeRow>()

            Log.d("WORKOUT_REPOSITORY", "typeId = ${typeRow.typeId} for $dbTypeName")
            typeRow.typeId
        } catch (e: Exception) {
            Log.e("WORKOUT_REPOSITORY", "Feil ved henting av type_id: ${e.message}", e)
            null
        }
    }

    suspend fun saveWorkout(workout: Workout): Boolean {
        return try {
            val userId = getCurrentDbUserId() ?: return false
            val typeId = getTrainingTypeId(workout.exerciseType) ?: return false

            val isoDate = workout.date + "T12:00:00Z"

            val payload = TrainingSessionInsert(
                userId = userId,
                typeId = typeId,
                distanceKm = workout.distanceKm.toDouble(),
                durationMinutes = workout.durationMinutes,
                trainingDate = isoDate
            )

            Log.d("WORKOUT_REPOSITORY", "Insert payload = $payload")

            supabase
                .from("training_sessions")
                .insert(payload)

            true
        } catch (e: Exception) {
            Log.e("WORKOUT_REPOSITORY", "Feil ved lagring av workout: ${e.message}", e)
            throw e
        }
    }

    suspend fun getWorkouts(): List<Workout> {
        return try {
            val userId = getCurrentDbUserId() ?: return emptyList()

            val sessions = supabase
                .from("training_sessions")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<TrainingSessions>()

            val types = supabase
                .from("training_types")
                .select()
                .decodeList<TrainingTypeRow>()

            val typeMap = types.associateBy { it.typeId }

            sessions.map { session ->
                val typeName = typeMap[session.typeId]?.typeName ?: "Running"

                Workout(
                    id = session.sessionId,
                    exerciseType = dbNameToExerciseType(typeName),
                    distanceKm = session.distanceKm.toFloat(),
                    durationMinutes = session.durationMinutes ?: 0,
                    date = session.trainingDate?.take(10) ?: ""
                )
            }.sortedByDescending { it.date }
        } catch (e: Exception) {
            Log.e("WORKOUT_REPOSITORY", "Feil ved henting av workouts: ${e.message}", e)
            emptyList()
        }
    }
}