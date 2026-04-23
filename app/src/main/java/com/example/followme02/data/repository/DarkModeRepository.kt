package com.example.followme02.data.repository

import android.util.Log
import com.example.followme02.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class DarkModeRow(
    @SerialName("darkmode")
    val darkMode: Boolean = false
)

class DarkModeRepository {

    private val supabase = SupabaseProvider.client

    suspend fun loadDarkModeForCurrentUser(): Boolean? {
        return try {
            val authUserId = supabase.auth.currentUserOrNull()?.id
                ?: return null

            supabase
                .from("users")
                .select(columns = Columns.list("darkmode")) {
                    filter {
                        eq("auth_id", authUserId)
                    }
                }
                .decodeList<DarkModeRow>()
                .firstOrNull()
                ?.darkMode ?: false
        } catch (e: Exception) {
            Log.e("DARK_MODE_REPOSITORY", "Error loading darkmode", e)
            null
        }
    }

    suspend fun saveDarkModeForCurrentUser(enabled: Boolean) {
        try {
            val authUserId = supabase.auth.currentUserOrNull()?.id
                ?: return

            supabase
                .from("users")
                .update({
                    set("darkmode", enabled)
                }) {
                    filter {
                        eq("auth_id", authUserId)
                    }
                }

            Log.d("DARK_MODE_REPOSITORY", "Saved darkmode=$enabled")
        } catch (e: Exception) {
            Log.e("DARK_MODE_REPOSITORY", "Error saving darkmode", e)
        }
    }
}