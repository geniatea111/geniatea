package com.example.compose.geniatea.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Reference design dimensions (e.g., standard Android design width)
const val REFERENCE_WIDTH = 375
const val REFERENCE_HEIGHT = 812

@Composable
fun rememberWindowInfo(): WindowInfo {
    val configuration = LocalConfiguration.current
    return WindowInfo(
        screenWidthInfo = when {
            configuration.screenWidthDp < 600 -> WindowInfo.WindowType.Compact
            configuration.screenWidthDp < 840 -> WindowInfo.WindowType.Medium
            else -> WindowInfo.WindowType.Expanded
        },
        screenHeightInfo = when {
            configuration.screenHeightDp < 480 -> WindowInfo.WindowType.Compact
            configuration.screenHeightDp < 900 -> WindowInfo.WindowType.Medium
            else -> WindowInfo.WindowType.Expanded
        },
        screenWidth = configuration.screenWidthDp.dp,
        screenHeight = configuration.screenHeightDp.dp
    )
}

data class WindowInfo(
    val screenWidthInfo: WindowType,
    val screenHeightInfo: WindowType,
    val screenWidth: Dp,
    val screenHeight: Dp
) {
    sealed class WindowType {
        object Compact : WindowType()
        object Medium : WindowType()
        object Expanded : WindowType()
    }
}

// Extension functions for responsive sizing
// Extension functions for responsive sizing
@Composable
fun Int.sdp(): Dp {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = if (screenWidth <= 360) {
        (screenWidth / REFERENCE_WIDTH) * 0.85f // Scale down more on small screens
    } else {
        screenWidth / REFERENCE_WIDTH
    }
    return (this * scale).dp
}

@Composable
fun Double.sdp(): Dp {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = if (screenWidth <= 360) {
        (screenWidth / REFERENCE_WIDTH) * 0.85f
    } else {
        screenWidth / REFERENCE_WIDTH
    }
    return (this * scale).dp
}

@Composable
fun Int.ssp(): TextUnit {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    // SCALING STRATEGY:
    // Uses width for scaling text to keep it proportional to the container width.
    val scale = if (screenWidth <= 360) {
        (screenWidth / REFERENCE_WIDTH) * 0.85f
    } else {
        screenWidth / REFERENCE_WIDTH
    }
    return (this * scale).sp
}
@Composable
fun Double.ssp(): TextUnit {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = if (screenWidth <= 360) {
        (screenWidth / REFERENCE_WIDTH) * 0.85f
    } else {
        screenWidth / REFERENCE_WIDTH
    }
    return (this * scale).sp
}
