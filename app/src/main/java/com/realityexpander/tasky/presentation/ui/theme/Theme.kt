package com.realityexpander.tasky.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DarkColorPalette = darkColors(
    primary = Color.White,
    primaryVariant = Color(0xFF8E97FD),
    secondary = Teal200,
    background = Color.Black,
    surface = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val LightColorPalette = lightColors(
    primary = Color.Black,
    primaryVariant = Color(0xFF8E97FD),
    secondary = Teal200,
    background = Color.White,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,

    /* Other default colors to override
    onPrimary = Color.White,
    onSecondary = Color.Black,
    */
)

@Composable
fun TaskyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }


    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
fun textEntryFieldTextStyle() = Typography.h6.copy(
    fontFamily = fonts,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    color = colors.primary,
)

