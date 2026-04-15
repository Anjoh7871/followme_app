package com.example.followme02.data.repository

import android.util.Log
import com.example.followme02.data.remote.SupabaseProvider
import com.example.followme02.model.Destinations
import com.example.followme02.model.UserJourneySelectionResponse
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class DestinationRepository {

    private val client = SupabaseProvider.client

    suspend fun getAllDestinations(): List<Destinations> {
        return try {
            client
                .from("destinations")
                .select()
                .decodeList<Destinations>()
                .sortedBy { it.kmThreshold }
        } catch (e: Exception) {
            Log.e("DESTINATION_REPOSITORY", "Error loading destinations", e)
            emptyList()
        }
    }

    suspend fun getCurrentJourneySelection(): UserJourneySelectionResponse? {
        return try {
            val authId = client.auth.currentUserOrNull()?.id ?: return null

            client
                .from("users")
                .select(columns = Columns.list("selected_destination_id", "journey_start_km")) {
                    filter {
                        eq("auth_id", authId)
                    }
                }
                .decodeSingleOrNull<UserJourneySelectionResponse>()
        } catch (e: Exception) {
            Log.e("DESTINATION_REPOSITORY", "Error getting current journey selection", e)
            null
        }
    }

    suspend fun saveSelectedDestination(
        destinationId: Int,
        currentTotalKm: Double
    ): Boolean {
        return try {
            val authId = client.auth.currentUserOrNull()?.id ?: return false

            client
                .from("users")
                .update(
                    {
                        set("selected_destination_id", destinationId)
                        set("journey_start_km", currentTotalKm)
                    }
                ) {
                    filter {
                        eq("auth_id", authId)
                    }
                }

            true
        } catch (e: Exception) {
            Log.e("DESTINATION_REPOSITORY", "Error saving selected destination", e)
            false
        }
    }

    suspend fun clearCurrentJourney(): Boolean {
        return try {
            val authId = client.auth.currentUserOrNull()?.id ?: return false

            client
                .from("users")
                .update(
                    {
                        set("selected_destination_id", null as Int?)
                        set("journey_start_km", null as Double?)
                    }
                ) {
                    filter {
                        eq("auth_id", authId)
                    }
                }

            true
        } catch (e: Exception) {
            Log.e("DESTINATION_REPOSITORY", "Error clearing current journey", e)
            false
        }
    }
}