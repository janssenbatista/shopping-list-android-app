package dev.janssenbatista.shoppinglist.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

enum class WindowSize { Compact, Medium, Expanded }

@Composable
fun getWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    return when {
        screenWidth < 600.dp -> WindowSize.Compact
        screenWidth < 840.dp -> WindowSize.Medium
        else -> WindowSize.Expanded
    }
}