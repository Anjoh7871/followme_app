package com.example.followme02.data.repository

import android.util.Log
import com.example.followme02.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardUser(
    @SerialName("user_id")
    val userId: Int,

    val username: String,

    @SerialName("total_points")
    val totalPoints: Int = 0,

    @SerialName("level_id")
    val currentLevel: Int = 1,

    val totalAccumulatedKm: Double = 0.0
)

@Serializable
data class UserDistanceRow(
    @SerialName("distance_km")
    val distanceKm: Double = 0.0
)

@Serializable
data class DbUserIdRow(
    @SerialName("user_id")
    val userId: Int
)

@Serializable
data class FriendshipRow(
    @SerialName("user_id_1")
    val userId1: Int,

    @SerialName("user_id_2")
    val userId2: Int,

    @SerialName("status_id")
    val statusId: Int
)

class LeaderboardRepository {

    private val supabase = SupabaseProvider.client

    private suspend fun getCurrentDbUserId(): Int? {
        return try {
            val authUserId = supabase.auth.currentUserOrNull()?.id ?: return null

            val result = supabase
                .from("users")
                .select {
                    filter { eq("auth_id", authUserId) }
                }
                .decodeSingle<DbUserIdRow>()

            result.userId

        } catch (e: Exception) {
            Log.e("LEADERBOARD", "Error getting db user id", e)
            null
        }
    }

    suspend fun getLeaderboardUsers(): List<LeaderboardUser> {
        return try {
            val currentUserId = getCurrentDbUserId() ?: return emptyList()

            val friendships1 = supabase
                .from("friendships")
                .select {
                    filter {
                        eq("user_id_1", currentUserId)
                        eq("status_id", 2)
                    }
                }
                .decodeList<FriendshipRow>()

            val friendships2 = supabase
                .from("friendships")
                .select {
                    filter {
                        eq("user_id_2", currentUserId)
                        eq("status_id", 2)
                    }
                }
                .decodeList<FriendshipRow>()

            val friendIds =
                friendships1.map { it.userId2 } +
                        friendships2.map { it.userId1 }

            val allIds = (listOf(currentUserId) + friendIds).distinct()

            val users = mutableListOf<LeaderboardUser>()

            for (id in allIds) {
                val user = supabase
                    .from("users")
                    .select {
                        filter { eq("user_id", id) }
                    }
                    .decodeSingle<LeaderboardUser>()

                val sessions = supabase
                    .from("training_sessions")
                    .select {
                        filter { eq("user_id", id) }
                    }
                    .decodeList<UserDistanceRow>()

                val km = sessions.sumOf { it.distanceKm }

                users.add(user.copy(totalAccumulatedKm = km))
            }

            users.sortedByDescending { it.totalPoints }

        } catch (e: Exception) {
            Log.e("LEADERBOARD", "Error loading leaderboard", e)
            emptyList()
        }
    }
}