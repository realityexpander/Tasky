package com.realityexpander.tasky.agenda_feature.presentation.event_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.realityexpander.tasky.core.presentation.common.modifiers.tinyWidth
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme

@Composable
fun RowScope.PillButton(
    text : String,
    isSelected : Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text,
        textAlign = TextAlign.Center,
        fontWeight =
        if (isSelected)
            FontWeight.SemiBold
        else
            FontWeight.Normal,
        color =
        if (isSelected)
            MaterialTheme.colors.surface
        else
            MaterialTheme.colors.onSurface,
        modifier = Modifier
            .weight(1f)
            .clip(shape = RoundedCornerShape(15.dp))
            .background(
                if (isSelected)
                    MaterialTheme.colors.onSurface
                else
                    MaterialTheme.colors.onSurface.copy(alpha = .2f)
            )
            .padding(5.dp)
            .clickable {
                onClick()
            }
    )
}

@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(shape = RoundedCornerShape(15.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = .8f))
            .clickable(onClick = onClick)
            .padding(top = 2.dp, bottom = 2.dp)
    ) {
        Text(
            text,
            color = MaterialTheme.colors.surface,
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 150,
)
@Composable
fun PillButtonPreview() {
    TaskyTheme {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
        ) {
            PillButton(
                text = "Sure",
                isSelected = false,
                onClick = {},
                modifier = Modifier
            )
            Spacer(modifier = Modifier.tinyWidth())

            PillButton(
                text = "Thing",
                isSelected = true,
                onClick = {},
                modifier = Modifier
            )
        }
    }
}