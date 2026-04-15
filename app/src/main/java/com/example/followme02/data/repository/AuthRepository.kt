package com.example.followme02.data.repository

import android.util.Log
import com.example.followme02.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class AuthRepository @Inject constructor() {

    private val supabase = SupabaseProvider.client

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Boolean {
        return try {
            Log.d("REGISTER", "Starter registrering for: $email")

            // 1. Registrer i Supabase Auth
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password

                data = buildJsonObject {
                    put("username", username)
                }
            }

            // 2. Hent auth-id fra session
            val authId = supabase.auth.currentUserOrNull()?.id

            if (authId == null) {
                Log.e("REGISTER", "Auth user ID is null after signup")
                return false
            }

            Log.d("REGISTER", "Auth user after signup: $authId")

            // 3. Legg inn i users-tabellen
            supabase.from("users").insert(
                mapOf(
                    "auth_id" to authId,
                    "username" to username,
                    "email" to email,
                    "current_level" to 1,
                    "total_points" to 0,
                    "total_accumulated_km" to 0.0,
                    "is_active" to true
                )
            )

            Log.d("REGISTER", "Inserted user row with auth_id = $authId")
            true

        } catch (e: Exception) {
            Log.e("REGISTER", "ERROR: ${e.message}", e)
            false
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Boolean {
        return try {
            Log.d("LOGIN", "Prøver login for: $email")

            // Rydd bort eventuell gammel session først
            try {
                supabase.auth.signOut()
            } catch (_: Exception) {
            }

            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val currentUser = supabase.auth.currentUserOrNull()

            Log.d("LOGIN", "Current auth user email after login: ${currentUser?.email}")
            Log.d("LOGIN", "Current auth user id after login: ${currentUser?.id}")

            currentUser != null

        } catch (e: Exception) {
            Log.e("LOGIN", "ERROR: ${e.message}", e)
            false
        }
    }

    suspend fun logout(): Boolean {
        return try {
            supabase.auth.signOut()
            Log.d("LOGOUT", "Logout success")
            true
        } catch (e: Exception) {
            Log.e("LOGOUT", "ERROR: ${e.message}", e)
            false
        }
    }

    fun getCurrentUserEmail(): String? {
        return supabase.auth.currentUserOrNull()?.email
    }

    fun getCurrentUserId(): String? {
        return supabase.auth.currentUserOrNull()?.id
    }
}