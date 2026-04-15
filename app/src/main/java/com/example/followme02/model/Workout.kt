package com.example.followme02.model

data class Workout(
    val id: Int,
    val exerciseType: ExerciseType,
    val distanceKm: Float,
    val durationMinutes: Int,
    val date: String
)