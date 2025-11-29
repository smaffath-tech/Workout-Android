package com.sma.workoutapp

/**
 * A utility class intended to hold the logic and state management
 * for the "Add New Workout" screen/dialog.
 *
 * In a more complex app, this would be a ViewModel, but for simplicity,
 * we will use this class to interface between the UI and the Repository.
 *
 * @param repo The ProgressRepo instance used for saving data.
 */
class WorkoutScreen(private val repo: ProgressRepo) {

    // List of available workout types for the UI dropdown/spinner
    val workoutTypes = listOf(
        "Running",
        "Lifting",
        "Yoga",
        "Cycling",
        "Swimming",
        "Walking"
    )

    /**
     * Attempts to validate user input and save a new workout to the database.
     *
     * @param type The type of workout selected (String).
     * @param durationText The duration input from the UI (String).
     * @param caloriesText The calories input from the UI (String).
     * @return True if the save was successful (input was valid), false otherwise.
     */
    fun saveNewWorkout(
        type: String,
        durationText: String,
        caloriesText: String
    ): Boolean {
        // Simple input validation
        val duration = durationText.toIntOrNull()
        val calories = caloriesText.toIntOrNull()

        if (type.isBlank() || duration == null || calories == null || duration <= 0 || calories <= 0) {
            // In a real app, you would display a user-friendly error message here.
            return false
        }

        // 1. Create the new WorkoutData object
        val newWorkout = WorkoutData(
            // ID is temporary/placeholder, as the repository generates the final Firebase ID.
            id = "temp_id",
            type = type,
            durationMinutes = duration,
            caloriesBurned = calories,
            date = java.util.Date() // Use current time for the workout
        )

        // 2. Pass the data to the repository for persistence
        repo.addWorkout(newWorkout)

        return true
    }
}
