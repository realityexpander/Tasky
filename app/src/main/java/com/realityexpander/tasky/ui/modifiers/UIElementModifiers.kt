package com.realityexpander.tasky.ui.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Example:
// Box (
//    modifier = Modifier
//        .size(100.dp)
//        .redBall()
//    )

// stateless
fun Modifier.redBall(): Modifier =
    clip(CircleShape)
        .background(Color.Red)

// Example:
// Box (
//    modifier = Modifier
//        .size(100.dp)
//        .colorBall(Color.Green)
//    )

// stateful basic
fun Modifier.colorBall(color: Color = Color.Blue): Modifier =
    padding(10.dp)
        .clip(CircleShape)
        .background(color)

