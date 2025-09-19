package com.example.venueSelector

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable


@Serializable
data class VenueWrapper(val venues: List<Venue>) {
    companion object {
        val json = Json { ignoreUnknownKeys = true }

        fun loadVenuesFromJson(context: Context): List<Venue> {
            Log.d("VenueLoader", "Loading all venues from JSON file")

            val inputStream = context.assets.open("venues.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            return json.decodeFromString<VenueWrapper>(jsonString).venues
        }
    }
}
@Serializable
data class Venue(
    val date: String,
    val time: String,
    val banner_url: String,
    val location: String,
    val shop_name: String,
    val address: String,
    val location_link: String,
    val open_time: String
)
