package com.example.followme02.screen.home

import org.osmdroid.util.GeoPoint

data class MapPointUI(
    val name: String,
    val location: GeoPoint,
    val threshold: Double,
    val isVisited: Boolean
)