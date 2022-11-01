package com.realityexpander.tasky.core.presentation.common.modifiers

import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

// Example:
// Box (
//    modifier = Modifier
//        .size(100.dp)
//        .rotating(1000)
//    )


// stateful advanced - animation
fun Modifier.rotating(durationMillis: Int): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val angleRatio by transition.animateFloat (
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
        )
    )
    graphicsLayer {
        rotationZ = 360 * angleRatio
    }
}