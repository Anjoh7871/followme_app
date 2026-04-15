package com.example.followme02.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseProvider {

    val client = createSupabaseClient(
        supabaseUrl = "https://ikmvcufaujwtiiyrougk.supabase.co",
        supabaseKey = "sb_publishable_MRk4VJ7IxU9PeOM2F6GYow_n7j8sSyt"
    ) {
        install(Auth)
        install(Postgrest)
    }

}