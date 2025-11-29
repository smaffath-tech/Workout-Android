package com.sma.workoutapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sma.workoutapp.databinding.ActivityMainBinding

/**
 * The main entry point for the Workout Application.
 * This class handles the initialization and lifecycle of the primary screen.
 */
class MainActivity : AppCompatActivity() {

    // Declare a variable for View Binding. This allows us to access
    // elements in the layout (activity_main.xml) easily and safely.
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize View Binding
        // ActivityMainBinding is generated automatically based on your layout file (activity_main.xml).
        binding = ActivityMainBinding.inflate(layoutInflater)

        // 2. Set the content view to the root view obtained from the binding object.
        setContentView(binding.root)

        // --- Application Logic Starts Here ---

        // Example: If you had a button with ID 'myButton' in activity_main.xml,
        // you would access it like this:
        // binding.myButton.setOnClickListener {
        //     // Handle button click
        // }

        // TODO: Add logic here to display workout data, handle navigation, etc.
    }

    // You can add other lifecycle methods (like onResume, onPause) or custom functions below.
}
