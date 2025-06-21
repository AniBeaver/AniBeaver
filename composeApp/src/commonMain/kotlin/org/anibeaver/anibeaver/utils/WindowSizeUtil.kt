package org.anibeaver.anibeaver.utils

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Composable utility to calculate and remember the window size class.
 * This helps in adapting layouts based on screen size (Compact, Medium, Expanded).
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    // calculateWindowSizeClass should ideally be called from an Activity or similar context
    // in commonMain, we might need to pass activity or context if available,
    // or rely on Compose runtime to provide necessary context for desktop/multiplatform.
    // For now, this direct call works in newer Compose versions for Desktop and Android.
    return calculateWindowSizeClass()
}
