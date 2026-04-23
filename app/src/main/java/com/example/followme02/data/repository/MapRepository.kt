package com.example.followme02.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.osmdroid.util.GeoPoint
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/*suspend fun geocodeCity(cityName: String): GeoPoint? {
    val url = "https://nominatim.openstreetmap.org/search?q=$cityName&format=json&limit=1"

    return try {
        val response = URL(url).readText()
        val jsonArray = JSONArray(response)

        if (jsonArray.length() > 0) {
            val obj = jsonArray.getJSONObject(0)
            val lat = obj.getDouble("lat")
            val lon = obj.getDouble("lon")
            GeoPoint(lat, lon)
        } else null
    } catch (e: Exception) {
        null
    }
}

 */

class MapRepository {

    private val geoCache = mutableMapOf<String, GeoPoint>()
    suspend fun geocodeCity(city: String): GeoPoint? {

        // 1. CACHE CHECK (FAST PATH)
        geoCache[city]?.let {
            Log.d("GEOCODE", "Cache hit: $city")
            return it
        }

        return try {

            val encoded = URLEncoder.encode(city, "UTF-8")

            val url = URL(
                "https://nominatim.openstreetmap.org/search?q=$encoded&format=json&limit=1"
            )

            val connection = withContext(Dispatchers.IO) {
                url.openConnection() as HttpURLConnection
            }

            connection.setRequestProperty("User-Agent", "YourApp")

            val response = withContext(Dispatchers.IO) {
                connection.inputStream.bufferedReader().readText()
            }

            val json = JSONArray(response)

            if (json.length() > 0) {
                val obj = json.getJSONObject(0)

                val geo = GeoPoint(
                    obj.getDouble("lat"),
                    obj.getDouble("lon")
                )

                // 2. STORE IN CACHE
                geoCache[city] = geo

                geo
            } else null

        } catch (e: Exception) {
            Log.e("GEOCODE", "Failed for $city", e)
            null
        }
    }
}
