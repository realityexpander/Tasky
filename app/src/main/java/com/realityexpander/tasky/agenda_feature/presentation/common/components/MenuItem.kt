package com.realityexpander.tasky.agenda_feature.presentation.common.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Textsms
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.realityexpander.tasky.core.presentation.common.modifiers.DP
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme

@Composable
fun MenuItem(
    title: String,
    vectorIcon: ImageVector? = null,
    painterIcon: Painter? = null,
    iconContentDescription: String? = "Menu item $title",
    onClick: () -> Unit,
    isSelected: Boolean = false,
    isHighlighted: Boolean = false,
    enabled: Boolean = true,
    highlightColor: Color = MaterialTheme.colors.secondary,
) {
    DropdownMenuItem(
        enabled = enabled,
        onClick = {
            onClick()
        },
        modifier = Modifier
            .background(if(isHighlighted) highlightColor else MaterialTheme.colors.onSurface)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            color = if(enabled)
                    MaterialTheme.colors.surface
                else
                    MaterialTheme.colors.surface.copy(alpha = 0.2f),
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
        vectorIcon?.let {
            Icon(
                imageVector = vectorIcon,
                contentDescription = iconContentDescription,
                tint = if (enabled)
                    MaterialTheme.colors.surface
                else
                    MaterialTheme.colors.surface.copy(alpha = 0.2f),
                modifier = Modifier
                    .padding(DP.tiny)
                    .align(Alignment.CenterVertically)
            )
        }
        painterIcon?.let {
            Icon(
                painter = painterIcon,
                contentDescription = iconContentDescription,
                tint = if (enabled)
                    MaterialTheme.colors.surface
                else
                    MaterialTheme.colors.surface.copy(alpha = 0.2f),
                modifier = Modifier
                    .padding(DP.tiny)
                    .align(Alignment.CenterVertically)
            )
        }
        if(isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "is Selected",
                tint = if(enabled)
                        MaterialTheme.colors.surface
                    else
                        MaterialTheme.colors.surface.copy(alpha = 0.2f),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(DP.tiny)
            )
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    group = "Light mode"
)
@Composable
fun MenuItemPreviewPlain() {
    TaskyTheme {
        MenuItem(
            title = "Menu Item Plain",
            onClick = {},
            isSelected = false,
            isHighlighted = false,
            enabled = true,
        )
    }
}
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "Dark Mode"
)
@Composable
fun MenuItemPreviewPlainDark() {
    MenuItemPreviewPlain()
}

@Preview(
    showBackground = true,
    group = "Light mode"
)
@Composable
fun MenuItemPreviewWIcon() {
    TaskyTheme {
        MenuItem(
            title = "Menu Item w/ icon",
            vectorIcon = Icons.Filled.Mail,
            iconContentDescription = "Menu Item Icon",
            onClick = {},
            isSelected = false,
            isHighlighted = false,
            enabled = true,
        )
    }
}
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "Dark Mode"
)
@Composable
fun MenuItemPreviewWIconDark() {
    MenuItemPreviewWIcon()
}

@Preview(
    showBackground = true,
    group = "Light mode"
)
@Composable
fun MenuItemPreviewDisabled() {
    TaskyTheme {
        MenuItem(
            title = "Disabled Item",
            vectorIcon = Icons.Filled.Textsms,
            iconContentDescription = "Menu Item Icon",
            onClick = {},
            isSelected = false,
            isHighlighted = false,
            enabled = false,
        )
    }
}
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "Dark Mode"
)
@Composable
fun MenuItemPreviewDisabledDark() {
    MenuItemPreviewDisabled()
}

@Preview(
    showBackground = true,
    group = "Light mode"
)
@Composable
fun MenuItemPreviewHighlighted() {
    TaskyTheme {
        MenuItem(
            title = "Highlighted Item",
            vectorIcon = Icons.Filled.Textsms,
            iconContentDescription = "Menu Item Icon",
            onClick = {},
            isSelected = false,
            isHighlighted = true,
            enabled = true,
        )
    }
}
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "Dark Mode"
)
@Composable
fun MenuItemPreviewHighlightedDark() {
    MenuItemPreviewHighlighted()
}

@Preview(
    showBackground = true,
    group = "Light mode"
)
@Composable
fun MenuItemPreviewSelected() {
    TaskyTheme {
        MenuItem(
            title = "Selected Item",
            vectorIcon = Icons.Filled.Textsms,
            iconContentDescription = "Menu Item Icon",
            onClick = {},
            isSelected = true,
            isHighlighted = false,
            enabled = true,
        )
    }
}
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "Dark Mode"
)
@Composable
fun MenuItemPreviewSelectedDark() {
    MenuItemPreviewSelected()
}
