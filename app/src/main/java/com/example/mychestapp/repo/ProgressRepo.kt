package com.sma.workoutapp

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.Date

/**
 * Repository class responsible for managing user authentication and
 * all data persistence (saving and loading workout data) using Firebase Firestore.
 * This class abstracts the data source away from the UI, following the Repository pattern.
 */
class ProgressRepo {
    // Constants for Firestore collection paths
    private val TAG = "ProgressRepo"
    private val WORKOUTS_COLLECTION = "workouts"

    // Firebase instances
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth
    private var userId: String? = null

    // MutableStateFlow to hold the stream of workout data in real-time
    private val _workouts = MutableStateFlow<List<WorkoutData>>(emptyList())

    // Public Flow to be observed by the UI (MainActivity)
    val workouts: Flow<List<WorkoutData>> = _workouts

    // Private property to hold the reference to the user's specific collection
    private lateinit var userWorkoutsRef: CollectionReference

    init {
        // 1. Listen for authentication state changes
        auth.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                // User is signed in, set the userId and initialize the database connection
                userId = currentUser.uid
                Log.d(TAG, "User signed in: $userId")
                initializeUserCollections()
            } else {
                // User is signed out, attempt to sign in anonymously (required by environment)
                signInAnonymously()
            }
        }
    }

    /**
     * Attempts to sign in anonymously. The environment requires an active user for database access.
     */
    private fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Anonymous sign in successful.")
                } else {
                    Log.e(TAG, "Anonymous sign in failed: ${task.exception?.message}")
                }
            }
    }

    /**
     * Initializes the Firestore collection reference specific to the authenticated user.
     * This follows the secure structure: /artifacts/{appId}/users/{userId}/workouts
     */
    private fun initializeUserCollections() {
        // IMPORTANT: The __app_id variable is provided by the execution environment.
        val appId = try {
            val appIdField = Class.forName("com.sma.workoutapp.BuildConfig").getField("__app_id")
            appIdField.get(null) as String
        } catch (e: Exception) {
            "default-app-id" // Fallback if not running in the environment
        }

        val safeUserId = userId ?: return // Should not happen if called after sign-in

        // Construct the Firestore path for private user data
        val collectionPath = "artifacts/$appId/users/$safeUserId/$WORKOUTS_COLLECTION"
        userWorkoutsRef = db.collection(collectionPath)
        Log.d(TAG, "Firestore collection initialized at: $collectionPath")

        // Start listening for real-time updates
        startRealtimeListener()
    }

    /**
     * Sets up a real-time listener (onSnapshot) to fetch all workout data
     * and update the Flow whenever the data changes in the database.
     */
    private fun startRealtimeListener() {
        userWorkoutsRef
            // Note: Sorting is done in-memory to avoid mandatory index creation errors in Firestore
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed: ${e.message}", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val loadedWorkouts = snapshot.documents.mapNotNull { document ->
                        // Convert the Firestore document map to the WorkoutData Kotlin object
                        try {
                            // Extract ID from document and convert the rest to WorkoutData
                            val data = document.toObject<WorkoutDataMap>()
                            if (data != null) {
                                // Map the Firestore-friendly structure back to the domain model
                                WorkoutData(
                                    id = document.id,
                                    type = data.type ?: "Unknown",
                                    durationMinutes = data.durationMinutes ?: 0,
                                    caloriesBurned = data.caloriesBurned ?: 0,
                                    date = data.date ?: Date() // Default to current date if missing
                                )
                            } else null
                        } catch (ex: Exception) {
                            Log.e(TAG, "Error converting document ${document.id}: ${ex.message}", ex)
                            null
                        }
                    }
                    // Sort the list by date in descending order (newest first) in memory
                    val sortedWorkouts = loadedWorkouts.sortedByDescending { it.date }
                    _workouts.value = sortedWorkouts
                    Log.d(TAG, "Workouts updated: Total ${sortedWorkouts.size} items.")
                }
            }
    }

    /**
     * Saves a new workout entry to Firestore.
     *
     * @param workout The WorkoutData object to save. The 'id' field is ignored for creation.
     */
    fun addWorkout(workout: WorkoutData) {
        if (userId == null) {
            Log.w(TAG, "Attempted to add workout before user was authenticated.")
            return
        }

        // Convert the domain model to a Firestore-friendly Map for saving
        val workoutMap = mapOf(
            "type" to workout.type,
            "durationMinutes" to workout.durationMinutes,
            "caloriesBurned" to workout.caloriesBurned,
            "date" to workout.date
            // The 'id' field is automatically provided by Firestore
        )

        userWorkoutsRef.add(workoutMap)
            .addOnSuccessListener { Log.d(TAG, "Workout added successfully: ${it.id}") }
            .addOnFailureListener { e -> Log.e(TAG, "Error adding workout: ${e.message}", e) }
    }
}

/**
 * Internal class used only for Firebase mapping.
 * Firestore requires public no-argument constructors and non-private fields.
 * This class mirrors the WorkoutData but is optimized for Firestore's requirements.
 */
data class WorkoutDataMap(
    // Firestore will automatically fill the document ID, but we exclude it from the map here
    var type: String? = null,
    var durationMinutes: Int? = null,
    var caloriesBurned: Int? = null,
    var date: Date? = null,
    var notes: String? = null
)
