package com.realityexpander.tasky.agenda_feature.presentation.event_screen.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.domain.Photo
import com.realityexpander.tasky.core.presentation.common.modifiers.DP
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PhotoModal(
    title: String,
    photo: Photo,
    isRemoveEnabled: Boolean = false,
    onRemove: () -> Unit,
    onCancel: () -> Unit,
) {

    var isConfirmRemove by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = .5f))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface)

        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DP.small)
            ) {

                // Close button
                Icon(
                    Icons.Outlined.Close,
                    tint = MaterialTheme.colors.onSurface,
                    contentDescription = stringResource(R.string.back_description),
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.CenterVertically)
                        .clickable { onCancel() }
                )

                // Title
                Text(
                    title,
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                )

                // Remove button
                Icon(
                    Icons.Outlined.Delete,
                    tint = if (isRemoveEnabled) MaterialTheme.colors.onSurface else Color.Transparent,
                    contentDescription = stringResource(R.string.remove_description),
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.CenterVertically)
                        .clickable(isRemoveEnabled) {
                            isConfirmRemove = true
                        }
                )
            }

            Divider()

            // Photo
            when (photo) {
                is Photo.Local -> {
                    AsyncImage(
                        model = photo.uri,
                        contentDescription = stringResource(id = R.string.event_description_photo),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentScale = ContentScale.Fit
                    )
                }
                is Photo.Remote -> {
                    AsyncImage(
                        model = photo.url,
                        contentDescription = stringResource(id = R.string.event_description_photo),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }

    Box( modifier = Modifier
        .fillMaxSize()
    ) {
        if (isConfirmRemove) {
            AlertDialog(
                onDismissRequest = { isConfirmRemove = false },
                title = { Text("Delete Photo?") },
                text = { Text("Are you sure you want to remove this photo?") },
                confirmButton = {
                    Button(
                        onClick = {
                            isConfirmRemove = false
                            onRemove()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                    ) {
                        Text("REMOVE")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { isConfirmRemove = false },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                    ) {
                        Text("CANCEL")
                    }
                }
            )
        }
    }
}


@Preview(
    showBackground = false,
    showSystemUi = false,
    widthDp = 400,
    heightDp = 400,
    group = "light mode"
)
@Composable
fun PhotoModalPreview() {
    TaskyTheme {
        PhotoModal(
            title = "Photo",
            photo = Photo.Remote("0001", "https://randomuser.me/api/portraits/men/75.jpg"),
            onRemove = {},
            onCancel = {},
        )
    }
}

@Preview(
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "dark mode"
)
@Composable
fun PreviewDark() {
    PhotoModalPreview()
}