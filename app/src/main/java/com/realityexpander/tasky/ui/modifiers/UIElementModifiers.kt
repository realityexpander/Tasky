package com.realityexpander.tasky.ui.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
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


fun Modifier.tinyHeight(): Modifier =
    this.height(4.dp)
fun Modifier.extraSmallHeight(): Modifier =
    this.height(8.dp)
fun Modifier.smallHeight(): Modifier =
    this.height(16.dp)
fun Modifier.mediumHeight(): Modifier =
    this.height(24.dp)
fun Modifier.largeHeight(): Modifier =
    this.height(32.dp)
fun Modifier.extraLargeHeight(): Modifier =
    this.height(64.dp)

fun Modifier.tinyWidth(): Modifier =
    this.height(4.dp)
fun Modifier.extraSmallWidth(): Modifier =
    this.height(8.dp)
fun Modifier.smallWidth(): Modifier =
    this.height(16.dp)
fun Modifier.mediumWidth(): Modifier =
    this.height(24.dp)
fun Modifier.largeWidth(): Modifier =
    this.height(32.dp)
fun Modifier.extraLargeWidth(): Modifier =
    this.height(64.dp)