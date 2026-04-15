package com.example.followme02.data.repository

import android.util.Log
import com.example.followme02.data.remote.SupabaseProvider
import com.example.followme02.model.Destinations
import com.example.followme02.model.JourneyUiModel
import com.example.followme02.model.UserIdResponse
import com.example.followme02.model.UserVisitedDestinations
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class JourneyRepository {

    private val client = SupabaseProvider.client

    suspend fun getCurrentDbUserId(): Int? {
        return try {
            val authId = client.auth.currentUserOrNull()?.id ?: return null

            val result = client
                .from("users")
                .select(columns = Columns.list("user_id")) {
                    filter {
                        eq("auth_id", authId)
                    }
                }
                .decodeSingleOrNull<UserIdResponse>()

            result?.userId
        } catch (e: Exception) {
            Log.e("JOURNEY_REPO", "Error getting db user id", e)
            null
        }
    }

    suspend fun insertCompletedJourney(destinationId: Int): Boolean {
        return try {
            val userId = getCurrentDbUserId() ?: return false

            Log.d("JOURNEY_REPO", "Trying to insert visited destination. userId=$userId, destinationId=$destinationId")

            client.from("user_visited_destinations").insert(
                mapOf(
                    "user_id" to userId,
                    "destination_id" to destinationId
                )
            )

            Log.d("JOURNEY_REPO", "Visited destination inserted successfully")
            true
        } catch (e: Exception) {
            Log.e("JOURNEY_REPO", "Error inserting completed journey", e)
            false
        }
    }

    suspend fun getCompletedJourneys(): List<JourneyUiModel> {
        return try {
            val userId = getCurrentDbUserId() ?: return emptyList()

            val visitedDestinations = client
                .from("user_visited_destinations")
                .select() {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<UserVisitedDestinations>()

            val destinations = client
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
            }
        } catch (e: Exception) {
            Log.e("JOURNEY_REPO", "Error loading completed journeys", e)
            emptyList()
        }
    }
}