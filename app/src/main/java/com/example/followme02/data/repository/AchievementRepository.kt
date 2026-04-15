package com.example.followme02.data.repository

import android.util.Log
import com.example.followme02.data.remote.SupabaseProvider
import com.example.followme02.model.Achievement
import com.example.followme02.model.TrainingSessions
import com.example.followme02.model.UserAchievements
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.websocket.WebSocketDeflateExtension.Companion.install
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import com.example.followme02.model.UserAchievementInsert

@Serializable
data class InternalIdResponse(@SerialName("user_id") val userId: Int)

class AchievementRepository {
    // 1. Use the SAME client as the Profile Repo
    private val supabase = SupabaseProvider.client

    suspend fun getInternalUserId(): Int? {
        val authUserId = supabase.auth.currentUserOrNull()?.id ?: return null

        // We try both columns, just like the Profile Repo does
        return try {
            // Attempt 1: Check auth_id column
            val byAuthId = try {
                supabase.from("users")
                    .select { filter { eq("auth_id", authUserId) } }
                    .decodeSingleOrNull<InternalIdResponse>()
            } catch (e: Exception) { null }

            if (byAuthId != null) return byAuthId.userId

            // Attempt 2: Check id column (fallback)
            val byId = try {
                supabase.from("users")
                    .select { filter { eq("user_id", authUserId) } }
                    .decodeSingleOrNull<InternalIdResponse>()
            } catch (e: Exception) { null }

            byId?.userId
        } catch (e: Exception) {
            Log.e("ACHIEVEMENT", "Critical fetch error: ${e.message}")
            null
        }
    }

    suspend fun getDefinitions(): List<Achievement> =
        try { supabase.from("achievements").select().decodeList() } catch (e: Exception) {
            Log.e("SUPABASE", "getDefinitions failed: ${e.message}")
            emptyList()
        }

    suspend fun getUnlocked(userId: Int): List<UserAchievements> =
        try { supabase.from("user_achievements").select { filter { eq("user_id", userId) } }.decodeList() }
        catch (e: Exception) { emptyList() }

    suspend fun getTrainingSessions(userId: Int): List<TrainingSessions> =
        try { supabase.from("training_sessions").select { filter { eq("user_id", userId) } }.decodeList() }
        catch (e: Exception) { emptyList() }

    suspend fun unlockAchievement(userId: Int, achievementId: Int): Boolean {
        return try {
            val payload = UserAchievementInsert(
                userId = userId,
                achievementId = achievementId
            )

            Log.d("ACHIEVEMENT_REPO", "Trying insert into user_achievements: $payload")

            supabase.from("user_achievements").insert(payload)

            Log.d("ACHIEVEMENT_REPO", "Insert success for achievementId=$achievementId")
            true
        } catch (e: Exception) {
            Log.e("ACHIEVEMENT_REPO", "Insert FAILED: ${e.message}", e)
            false
        }
    }
}
/*class AchievementRepository {
    private val supabase = SupabaseProvider.client

    private val postgrest = supabase.postgrest

    // Gets the Int user_id based on the Auth UUID
    /*suspend fun getInternalUserId(): Int? {
        val authId = supabase.auth.currentUserOrNull()?.id ?: return null
        return try {
            val user = postgrest["users"].select {
                filter { eq("id", authId) }
            }.decodeSingle<Map<String, Int>>()
            user["user_id"]
        } catch (e: Exception) { null }
    }*/
    /*suspend fun getInternalUserId(): Int? {
        // This gets the UUID from the logged-in session
        val authId = supabase.auth.currentUserOrNull()?.id ?: return null

        return try {
            val response = postgrest["users"].select {
                // Change "id" to "auth_id" to match your foreign key
                filter { eq("auth_id", authId) }
            }

            // Use decodeSingleOrNull to avoid crashes if the user isn't found
            val userData = response.decodeSingleOrNull<Map<String, Int>>()
            userData?.get("user_id")
        } catch (e: Exception) {
            println("ERROR fetching internal ID: ${e.message}")
            null
        }
    }*/
    // 1. Use their provider instead of creating a new client
    suspend fun getInternalUserId(): Int? {
        val authUserId = supabase.auth.currentUserOrNull()?.id ?: return null

        return try {
            // 2. Try fetching by 'auth_id' first (Like the Profile Repo)
            val byAuthId = try {
                postgrest["users"].select {
                    filter { eq("auth_id", authUserId) }
                }.decodeSingleOrNull<Map<String, JsonElement>>()
            } catch (e: Exception) { null }

            if (byAuthId != null) {
                return byAuthId["user_id"]?.jsonPrimitive?.int
            }

            // 3. Try fetching by 'id' as a fallback (Like the Profile Repo)
            val byId = try {
                postgrest["users"].select {
                    filter { eq("id", authUserId) }
                }.decodeSingleOrNull<Map<String, JsonElement>>()
            } catch (e: Exception) { null }

            byId?.get("user_id")?.jsonPrimitive?.int

        } catch (e: Exception) {
            Log.e("ACHIEVEMENT", "Error finding internal user_id: ${e.message}")
            null
        }
    }
    suspend fun getDefinitions(): List<Achievement> =
        try { postgrest["achievements"].select().decodeList() } catch (e: Exception) { emptyList() }

    suspend fun getUnlocked(userId: Int): List<UserAchievements> =
        try { postgrest["user_achievements"].select { filter { eq("user_id", userId) } }.decodeList() }
        catch (e: Exception) { emptyList() }

    suspend fun getTrainingSessions(userId: Int): List<TrainingSessions> =
        try { postgrest["training_sessions"].select { filter { eq("user_id", userId) } }.decodeList() }
        catch (e: Exception) { emptyList() }
}*/
