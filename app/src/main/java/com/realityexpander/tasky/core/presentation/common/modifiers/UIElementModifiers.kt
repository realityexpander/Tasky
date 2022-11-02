package com.realityexpander.tasky.core.presentation.common.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.realityexpander.tasky.core.presentation.theme.TaskyShapes


fun Modifier.taskyWideButton(color: Color = Color.White): Modifier =
    fillMaxWidth()
    .extraLargeHeight()
    .clip(shape = TaskyShapes.WideButtonRoundedCorners)
    .background(color = color)

fun Modifier.taskySmallButton(color: Color = Color.White): Modifier =
    extraLargeHeight()
    .extraLargeWidth()
    .clip(shape = TaskyShapes.MediumButtonRoundedCorners)
    .background(color = color)

fun Modifier.taskyScreenTopCorners(color: Color = Color.White): Modifier =
    fillMaxSize()
    .clip(shape = TaskyShapes.ScreenTopCorners)
    .background(color = color)
    .padding(DP.small)

object DP {
    val tiny       = 4.dp
    val extraSmall = 8.dp
    val small      = 16.dp
    val medium     = 24.dp
    val large      = 32.dp
    val extraLarge = 48.dp
    val huge       = 64.dp
}

fun Modifier.tinyHeight(): Modifier       = this.height(DP.tiny)
fun Modifier.extraSmallHeight(): Modifier = this.height(DP.extraSmall)
fun Modifier.smallHeight(): Modifier      = this.height(DP.small)
fun Modifier.mediumHeight(): Modifier     = this.height(DP.medium)
fun Modifier.largeHeight(): Modifier      = this.height(DP.large)
fun Modifier.extraLargeHeight(): Modifier = this.height(DP.extraLarge)
fun Modifier.hugeHeight(): Modifier       = this.height(DP.huge)

fun Modifier.tinyWidth(): Modifier       = this.width(DP.tiny)
fun Modifier.extraSmallWidth(): Modifier = this.width(DP.extraSmall)
fun Modifier.smallWidth(): Modifier      = this.width(DP.small)
fun Modifier.mediumWidth(): Modifier     = this.width(DP.medium)
fun Modifier.largeWidth(): Modifier      = this.width(DP.large)
fun Modifier.extraLargeWidth(): Modifier = this.width(DP.extraLarge)
fun Modifier.hugeWidth(): Modifier       = this.width(DP.huge)