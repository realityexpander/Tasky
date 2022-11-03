package com.realityexpander.tasky.agenda_feature.presentation.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.realityexpander.tasky.agenda_feature.presentation.common.util.getUserAcronym
import com.realityexpander.tasky.core.presentation.common.modifiers.DP
import com.realityexpander.tasky.core.presentation.theme.TaskyLightBlue

@Composable
fun UserAcronymCircle(
    modifier: Modifier = Modifier,
    username: String? = "??",
    color: Color = TaskyLightBlue,
    style: TextStyle = MaterialTheme.typography.h5,
    fontWeight: FontWeight = FontWeight.Bold,
    softWrap: Boolean = false,
    textAlign: TextAlign = TextAlign.End,
    circleBackgroundColor: Color = MaterialTheme.colors.surface,
) {
    val acronym = remember(username) {
        getUserAcronym(username ?: "??")
    }
    val circleBackColorNative = remember(circleBackgroundColor) {
        Color(circleBackgroundColor.toArgb())
    }

    Text(
        text = acronym,
        modifier = modifier
            .padding(DP.tiny)
            .drawBehind {
                drawCircle(
                    color = circleBackColorNative,
                    radius = this.size.maxDimension * .7f
                )},
        style = style,
        fontWeight = fontWeight,
        textAlign = textAlign,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Clip,
        softWrap = softWrap,
    )
}