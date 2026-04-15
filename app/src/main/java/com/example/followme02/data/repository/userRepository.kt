package com.example.followme02.data.repository

import android.util.Log
import com.example.followme02.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val username: String,
    val email: String,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

    @SerialName("level_id")
    val currentLevel: Int = 1,

    @SerialName("total_points")
    val totalPoints: Int = 0,

    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class TeamMembershipRow(
    @SerialName("team_id")
    val teamId: Int,

    @SerialName("user_id")
    val userId: Int
)

@Serializable
data class TeamRow(
    @SerialName("team_name")
    val teamName: String
)

class UserRepository {

    private val supabase = SupabaseProvider.client

    fun getCurrentUserId(): String? {
        return supabase.auth.currentUserOrNull()?.id
    }

    suspend fun getProfile(): UserProfile? {
        return try {
            val authUserId = supabase.auth.currentUserOrNull()?.id ?: return null

            val profile = supabase
                .from("users")
                .select {
                    filter {
                        eq("auth_id", authUserId)
                    }
                }
                .decodeSingle<UserProfile>()

            Log.d("PROFILE", "Loaded profile for ${profile.username}")
            profile
        } catch (e: Exception) {
            Log.e("PROFILE", "Error fetching profile", e)
            null
        }
    }

    suspend fun getCurrentDbUserId(): Int? {
        return try {
            val authUserId = supabase.auth.currentUserOrNull()?.id ?: return null

            val result = supabase
                .from("users")
                .select(columns = Columns.list("user_id")) {
                    filter {
                        eq("auth_id", authUserId)
                    }
                }
                .decodeSingle<DbUserIdRow>()

            Log.d("PROFILE", "Current DB user id = ${result.userId}")
            result.userId
        } catch (e: Exception) {
            Log.e("PROFILE", "Error getting db user id", e)
            null
        }
    }

    suspend fun getUserTeamName(): String {
        return try {
            val userId = getCurrentDbUserId() ?: return "Not in a team"

            Log.d("PROFILE", "Looking for team membership for user_id=$userId")

            val memberships = supabase
                .from("team_memberships")
                .select(columns = Columns.list("team_id", "user_id")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<TeamMembershipRow>()

            Log.d("PROFILE", "Memberships found: $memberships")

            val membership = memberships.firstOrNull() ?: return "Not in a team"

            val team = supabase
                .from("teams")
                .select(columns = Columns.list("team_name")) {
                    filter {
                        eq("team_id", membership.teamId)
                    }
                }
                .decodeSingle<TeamRow>()

            Log.d("PROFILE", "Resolved team name: ${team.teamName}")
            team.teamName
        } catch (e: Exception) {
            Log.e("PROFILE", "Error fetching team", e)
            "Not in a team"
        }
    }
}