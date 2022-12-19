package com.realityexpander.tasky.agenda_feature.presentation.common.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.realityexpander.tasky.R
import com.realityexpander.tasky.core.presentation.common.modifiers.DP
import com.realityexpander.tasky.core.presentation.theme.TaskyGreen
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditTextModal(
    title: String,
    text: String,
    editTextStyle: TextStyle =  MaterialTheme.typography.body1,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
) {

    var editingText by remember {
        mutableStateOf( TextFieldValue(text, TextRange(start = 0, text.length)) )
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }

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

                // Back caret icon
                Icon(
                    Icons.Outlined.ChevronLeft,
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

                // Save button
                Text(
                    "Save",
                    style = MaterialTheme.typography.h5,
                    color = TaskyGreen,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .clickable {
                            onSave(editingText.text)
                        }
                )
            }

            Divider()

            // Text field
            TextField(
                value = editingText,
                isError = false,
                textStyle = editTextStyle.copy(textDecoration = null, color = MaterialTheme.colors.onSurface),
                onValueChange = { editingText = it },
                singleLine = false,
                maxLines = 20,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    textColor = MaterialTheme.colors.onSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = DP.small, bottom = DP.large, start = 0.dp, end = 0.dp)
                    .background(Color.Transparent)
                    .focusRequester(focusRequester)
            )
        }

    }
}


@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 400,
    heightDp = 400,
    group = "light mode"
)
@Composable
fun EditTextModalPreview() {
    TaskyTheme {
        EditTextModal(
            title = "EDIT DESCRIPTION",
            text = LoremIpsum(20).values.joinToString { it },
            onSave = {},
            onCancel = {},
        )
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "dark mode"
)
@Composable
fun PreviewDark() {
    EditTextModalPreview()
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "dark mode"
)
@Composable
fun PreviewDarkLargeText() {
    TaskyTheme {
        EditTextModal(
            title = "EDIT TITLE",
            text = LoremIpsum(20).values.joinToString { it },
            onSave = {},
            onCancel = {},
            editTextStyle = MaterialTheme.typography.h2
        )
    }
}