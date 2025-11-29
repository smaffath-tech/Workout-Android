package com.sma.workoutapp

import java.util.Date

/**
 * Data class representing a single recorded workout session.
 * Data classes automatically provide useful functions like equals(), hashCode(),
 * toString(), and copy(), which is perfect for modeling data.
 *
 * @property id A unique identifier for the workout (e.g., a Firebase ID or UUID).
 * @property type The type of workout (e.g., "Running", "Lifting", "Yoga").
 * @property durationMinutes The length of the workout in minutes.
 * @property caloriesBurned The estimated number of calories burned.
 * @property date The timestamp when the workout was completed.
 * @property notes Any additional notes the user wants to add.
 */
data class WorkoutData(
    val id: String,
    val type: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val date: Date,
    val notes: String? = null // Optional field
)
