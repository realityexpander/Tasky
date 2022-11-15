package com.realityexpander.tasky.agenda_feature.presentation.event_screen.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.realityexpander.tasky.core.presentation.common.modifiers.smallHeight

@Composable
fun SmallHeightHorizontalDivider() {
    Spacer(modifier = Modifier.smallHeight())
    Divider(
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.smallHeight())
}