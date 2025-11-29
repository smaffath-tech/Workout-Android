package com.sma.workoutapp

import androidx.compose.ui.graphics.Color

/**
 * Defines standard colors for the application theme,
 * making them accessible across the project.
 *
 * NOTE: This uses the androidx.compose.ui.graphics.Color object, which is primarily
 * intended for Jetpack Compose, but serves as a clear way to define colors in Kotlin.
 * If you are strictly using XML layouts, you should primarily rely on res/values/colors.xml.
 */
object AppColors {
    val Primary = Color(0xFF6200EE) // A deep purple for the main branding
    val PrimaryDark = Color(0xFF3700B3) // Darker shade for status bar
    val Secondary = Color(0xFF03DAC6) // Teal color for accents and interactive elements
    val Background = Color(0xFFFFFFFF) // White background
    val Surface = Color(0xFFF5F5F5) // Light gray for card surfaces
    val Error = Color(0xFFB00020) // Red for error messages
    val OnPrimary = Color(0xFFFFFFFF) // Text color on Primary background
    val OnSecondary = Color(0xFF000000) // Text color on Secondary background
}
