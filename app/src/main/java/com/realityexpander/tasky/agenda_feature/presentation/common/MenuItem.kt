package com.realityexpander.tasky.agenda_feature.presentation.common

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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.realityexpander.tasky.core.presentation.common.modifiers.DP

@Composable
fun MenuItem(
    title: String,
    icon: ImageVector? = null,
    iconContentDescription: String? = "Menu item $title",
    onClick: () -> Unit,
    isSelected: Boolean = false,
    isHighlighted: Boolean = false,
    enabled: Boolean = true,
) {
    DropdownMenuItem(
        enabled = enabled,
        onClick = {
            onClick()
        },
        modifier = Modifier
            .drawBehind {
                if (isHighlighted) drawRect(Color.LightGray)
            }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            color = if(enabled)
                    MaterialTheme.colors.surface
                else
                    MaterialTheme.colors.surface.copy(alpha = 0.3f),
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
        if(icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = iconContentDescription,
                tint = if(enabled)
                        MaterialTheme.colors.surface
                    else
                        MaterialTheme.colors.surface.copy(alpha = 0.3f),
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
                        MaterialTheme.colors.surface.copy(alpha = 0.3f),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(DP.tiny)
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0x00000000,
)
@Composable
fun MenuItemPreviewPlain() {
    MenuItem(
        title = "Menu Item Plain",
        onClick = {},
        isSelected = false,
        isHighlighted = false,
        enabled = true,
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0x00000000,
)
@Composable
fun MenuItemPreview() {
    MenuItem(
        title = "Menu Item w/ icon",
        icon = Icons.Filled.Mail,
        iconContentDescription = "Menu Item Icon",
        onClick = {},
        isSelected = false,
        isHighlighted = false,
        enabled = true,
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0x00000000,
)
@Composable
fun MenuItemPreviewDisabled() {
    MenuItem(
        title = "Disabled Item",
        icon = Icons.Filled.Textsms,
        iconContentDescription = "Menu Item Icon",
        onClick = {},
        isSelected = false,
        isHighlighted = false,
        enabled = false,
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0x00000000,
)
@Composable
fun MenuItemPreviewHighlighted() {
    MenuItem(
        title = "Highlighted Item",
        icon = Icons.Filled.Textsms,
        iconContentDescription = "Menu Item Icon",
        onClick = {},
        isSelected = false,
        isHighlighted = true,
        enabled = true,
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0x00000000,
)
@Composable
fun MenuItemPreviewSelected() {
    MenuItem(
        title = "Selected Item",
        icon = Icons.Filled.Textsms,
        iconContentDescription = "Menu Item Icon",
        onClick = {},
        isSelected = true,
        isHighlighted = false,
        enabled = true,
    )
}