package com.example.followme02.data.repository
import com.example.followme02.data.logic.PointsCalculator
import com.example.followme02.data.remote.SupabaseProvider
import com.example.followme02.model.ExerciseType
import io.github.jan.supabase.postgrest.from


class PointsRepository {

    private val supabase = SupabaseProvider.client

    suspend fun getUserPoints(userId: String): Int {
        val profile = supabase.from("profiles")
            .select { filter { eq("id", userId) } }
            //.decodeSingle<Profile>()

        return 100 //profile.total_points // <- Change into this later!!
    }

    suspend fun addPoints(
        userId: String,
        distance: Long,
        type: ExerciseType
    ) {
        val earnedPoints = PointsCalculator.calculate(distance, type)

        val currentProfile = supabase.from("profiles")
            .select { filter { eq("id", userId) } }
            //.decodeSingle<Profile>()

        /* FIX THIS LATER
        supabase.from("profiles").update(
            mapOf("total_points" to (currentProfile.total_points + earnedPoints))
        ) {
            filter { eq("id", userId) }
        }

         */
    }
}

