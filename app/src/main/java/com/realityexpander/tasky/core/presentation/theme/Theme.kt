package com.realityexpander.tasky.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.realityexpander.tasky.R

@Composable
private fun darkColorPalette(): Colors {

   return darkColors(
        primary = Color.White,
        onPrimary = Color.Black,
        primaryVariant = TaskyPurple,
        secondary = colorResource(id = R.color.tasky_green),
        onSecondary = Color.White,
        background = Color.Black,
        surface = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White,
    )
}

@Composable
private fun lightColorPalette(): Colors {
    return lightColors(
        primary = Color.Black,
        onPrimary = Color.White,
        primaryVariant = TaskyPurple,
        secondary = colorResource(id = R.color.tasky_green),
        onSecondary = Color.White,
        background = Color.White,
        surface = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black,
    )
}

@Composable
fun TaskyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        darkColorPalette()
    } else {
        lightColorPalette()
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
fun textEntryFieldTextStyle() = Typography.h5.copy(
    fontFamily = fonts,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    color = colors.primary,
)

