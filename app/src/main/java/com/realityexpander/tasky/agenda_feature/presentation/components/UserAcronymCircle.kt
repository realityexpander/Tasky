package com.realityexpander.tasky.agenda_feature.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    username: String? = null,
    color: Color = TaskyLightBlue,
    style: TextStyle = MaterialTheme.typography.h5,
    fontWeight: FontWeight = FontWeight.Bold,
    softWrap: Boolean = false,
    textAlign: TextAlign = TextAlign.End,
    circleBackgroundColor: Color = MaterialTheme.colors.surface,
) {
    val acronym = remember(username) {
        getUserAcronym(username)
    }
    val circleBackColorNative = remember(circleBackgroundColor) {
        Color(circleBackgroundColor.toArgb())
    }

    if(acronym.isNotEmpty()) {
        Text(
            modifier = modifier
                .padding(DP.tiny) // padding around the circle
                .clip(CircleShape).background(circleBackgroundColor)
                .padding(DP.extraSmall), // size of the circle
            text = acronym,
            style = style,
            fontWeight = fontWeight,
            softWrap = softWrap,
            textAlign = textAlign,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}