package com.example.followme02.data.repository

import android.util.Log
import com.example.followme02.data.remote.SupabaseProvider
import com.example.followme02.model.Destinations
import com.example.followme02.model.JourneyUiModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendDbUserRow(
    @SerialName("user_id")
    val userId: Int,

    val username: String,
    val email: String,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

    @SerialName("level_id")
    val levelId: Int = 1,

    @SerialName("total_points")
    val totalPoints: Int = 0,

    @SerialName("total_accumulated_km")
    val totalAccumulatedKm: Double = 0.0,

    @SerialName("selected_destination_id")
    val selectedDestinationId: Int? = null,

    @SerialName("journey_start_km")
    val journeyStartKm: Double? = null
)

@Serializable
private data class FriendAchievementRow(
    @SerialName("achievement_id")
    val achievementId: Int
)

@Serializable
private data class FriendVisitedDestinationRow(
    @SerialName("destination_id")
    val destinationId: Int,

    @SerialName("visited_at")
    val visitedAt: String? = null
)

@Serializable
private data class FriendDbUserIdRow(
    @SerialName("user_id")
    val userId: Int
)

class FriendProfileRepository {

    private val supabase = SupabaseProvider.client

    suspend fun getCurrentDbUserId(): Int? {
        return try {
            val authUserId = supabase.auth.currentUserOrNull()?.id ?: return null

            supabase
                .from("users")
                .select(columns = Columns.list("user_id")) {
                    filter {
                        eq("auth_id", authUserId)
                    }
                }
                .decodeList<FriendDbUserIdRow>()
                .firstOrNull()
                ?.userId
        } catch (e: Exception) {
            Log.e("FRIEND_PROFILE", "Error getting current db user id", e)
            null
        }
    }

    suspend fun getFriendUser(friendUserId: Int): FriendDbUserRow? {
        return try {
            supabase
                .from("users")
                .select(
                    columns = Columns.list(
                        "user_id",
                        "username",
                        "email",
                        "avatar_url",
                        "level_id",
                        "total_points",
                        "total_accumulated_km",
                        "selected_destination_id",
                        "journey_start_km"
                    )
                ) {
                    filter {
                        eq("user_id", friendUserId)
                    }
                }
                .decodeList<FriendDbUserRow>()
                .firstOrNull()
        } catch (e: Exception) {
            Log.e("FRIEND_PROFILE", "Error loading friend user", e)
            null
        }
    }

    suspend fun getAchievementsCount(friendUserId: Int): Int {
        return try {
            supabase
                .from("user_achievements")
                .select(columns = Columns.list("achievement_id")) {
                    filter {
                        eq("user_id", friendUserId)
                    }
                }
                .decodeList<FriendAchievementRow>()
                .size
        } catch (e: Exception) {
            Log.e("FRIEND_PROFILE", "Error loading achievements count", e)
            0
        }
    }

    suspend fun getDestinationById(destinationId: Int): Destinations? {
        return try {
            supabase
                .from("destinations")
                .select {
                    filter {
                        eq("destination_id", destinationId)
                    }
                }
                .decodeList<Destinations>()
                .firstOrNull()
        } catch (e: Exception) {
            Log.e("FRIEND_PROFILE", "Error loading destination", e)
            null
        }
    }

    suspend fun getCompletedJourneys(friendUserId: Int): List<JourneyUiModel> {
        return try {
            val visitedDestinations = supabase
                .from("user_visited_destinations")
                .select(columns = Columns.list("destination_id", "visited_at")) {
                    filter {
                        eq("user_id", friendUserId)
                    }
                }
                .decodeList<FriendVisitedDestinationRow>()

            val destinations = supabase
                .from("destinations")
                .select()
                .decodeList<Destinations>()

            visitedDestinations.mapNotNull { visited ->
                val destination = destinations.find { it.destinationId == visited.destinationId }

                destination?.let {
                    JourneyUiModel(
                        destinationId = it.destinationId,
                        destinationName = it.name,
                        km = it.kmThreshold,
                        completedAt = visited.visitedAt ?: "",
                        factText = it.factText,
                        imageUrl = it.imageUrl
                    )
                }
            }.sortedByDescending { it.completedAt }
        } catch (e: Exception) {
            Log.e("FRIEND_PROFILE", "Error loading completed journeys", e)
            emptyList()
        }
    }

    suspend fun removeFriend(friendUserId: Int): Boolean {
        return try {
            val currentUserId = getCurrentDbUserId() ?: return false
            if (currentUserId == friendUserId) return false

            val lowId = minOf(currentUserId, friendUserId)
            val highId = maxOf(currentUserId, friendUserId)

            supabase
                .from("friendships")
                .delete {
                    filter {
                        eq("user_id_1", lowId)
                        eq("user_id_2", highId)
                    }
                }

            true
        } catch (e: Exception) {
            Log.e("FRIEND_PROFILE", "Error removing friend", e)
            false
        }
    }
}