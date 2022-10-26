package com.realityexpander.tasky.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp
import com.realityexpander.tasky.ui.theme.modifiers.DP

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp),
)

object TaskyShapes {
    val ScreenTopCorners =
        RoundedCornerShape(topStart = DP.medium, topEnd = DP.medium)
    val WideButtonRoundedCorners =
        RoundedCornerShape(DP.medium)
}