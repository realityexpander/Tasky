package com.realityexpander.tasky.agenda_feature.presentation.add_event_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.agenda_feature.presentation.agenda_screen.AgendaEvent
import com.realityexpander.tasky.core.presentation.common.modifiers.*
import com.realityexpander.tasky.core.presentation.theme.TaskyGray
import com.realityexpander.tasky.core.presentation.theme.TaskyLightGreen
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme

@Composable
@Destination
fun AddEventScreen(
    navigator: DestinationsNavigator,
    viewModel: AddEventViewModel = hiltViewModel(),
) {

    val state by viewModel.addEventState.collectAsState()

    AddEventScreenContent(
        state = state,
        onAction = viewModel::sendEvent,
        navigator = navigator,
    )
}

@Composable
fun AddEventScreenContent(
    state: AddEventState,
    onAction: (AgendaEvent) -> Unit,
    navigator: DestinationsNavigator,
) {
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

//    val agendaItems by state.agendaItems.collectAsState(initial = emptyList())

    fun popBack() {
        navigator.popBackStack()

//        navigate(
//            LoginScreenDestination(
//                username = null,
//                email = null,
//                password = null,
//                confirmPassword = null,
//            )
//        ) {
//            popUpTo(LoginScreenDestination.route) {
//                inclusive = true
//            }
//            launchSingleTop = true
//            restoreState = true
//        }
    }

    // Handle stateful one-time events
    LaunchedEffect(state) {
//        if (state.scrollToItemId != null) {
//            val item = agendaItems.indexOfFirst { it.id == state.scrollToItemId }
//            if (item >= 0) {
//                scope.launch {
//                    scrollState.animateScrollToItem(item)
//
//            }
//            onAction(AgendaEvent.StatefulOneTimeEvent.ResetScrollTo)
//        }
    }

    // todo handle "un-stateful" one-time events (like Snackbar), will use a Channel or SharedFlow

    // • Main Container
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colors.onSurface)
            .padding(0.dp)
    ) col1@{
        Spacer(modifier = Modifier.mediumHeight())

        // • HEADER FOR SCREEN (Close, Current Date, Edit/Save)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = DP.small, end = DP.small)
        ) {

            // • Close button
            Icon(
                imageVector = Icons.Filled.Close,
                tint = MaterialTheme.colors.surface,
                contentDescription = "Close Add Event",
                modifier = Modifier
                    .size(30.dp)
                    .alignByBaseline()
                    .align(Alignment.CenterVertically)
            )

            // • TODAY'S DATE
            Text(
                "01 MARCH 2022",
                color = MaterialTheme.colors.surface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )

            // • Edit / Save Button
            Icon(
                imageVector = Icons.Filled.Edit,
                tint = MaterialTheme.colors.surface,
                contentDescription = "Edit Event",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .wrapContentWidth(Alignment.End)
            )

        }
        Spacer(modifier = Modifier.smallHeight())


        // • Event header / description
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
                .padding(0.dp)
        ) col2@{
            Spacer(modifier = Modifier.tinyHeight())

            // • Agenda Item Type (Event)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .offset(2.dp, 0.dp)
                        .background(color = TaskyLightGreen)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.extraSmallWidth())
                Text(
                    "Event",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.smallHeight())

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // • Visual Circle
                Icon(
                    imageVector = Icons.Outlined.Circle,
                    tint = MaterialTheme.colors.onSurface,
                    contentDescription = "Meeting title marker",
                    modifier = Modifier
                        .size(26.dp)
                        .align(Alignment.CenterVertically)
                        .offset(0.dp, 0.dp)
                )
                Spacer(modifier = Modifier.extraSmallWidth())
                Text(
                    "Event Title",
                    style = MaterialTheme.typography.h2,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.smallHeight())
            Divider(
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.smallHeight())

            // • Description
            Text(
                text = LoremIpsum(15).values.joinToString { it },
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
            Spacer(modifier = Modifier.smallHeight())
        }

        // • Photo Picker / Add / Remove
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TaskyGray)
                .padding(DP.small)
                .wrapContentHeight()
        ) {
            val photos = 0 // for testing only

            if (photos == 0) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(top = DP.large, bottom = DP.large)
                ) {
                    // • Add Photo Icon
                    Icon(
                        imageVector = Icons.Filled.Add,
                        tint = MaterialTheme.colors.onSurface.copy(alpha = .3f),
                        contentDescription = "Meeting title marker",
                        modifier = Modifier
                            .size(26.dp)
                    )
                    Spacer(modifier = Modifier.smallWidth())
                    Text(
                        "Add photos",
                        modifier = Modifier
                            .offset(y = 2.dp),
                        fontWeight = Bold,
                        color = MaterialTheme.colors.onSurface.copy(alpha = .3f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Photos",
                            style = MaterialTheme.typography.h3,
                            fontWeight = SemiBold,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.extraSmallHeight())

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {

                        (1..5).forEach {
                            // • Photo content box
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .background(TaskyGray)
                                    .border(
                                        2.dp,  // Border width
                                        MaterialTheme.colors.onSurface.copy(alpha = .3f),
                                        RoundedCornerShape(10.dp)
                                    )
                            ) {
                                // • Add Photo Icon
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    tint = MaterialTheme.colors.onSurface.copy(alpha = .3f),
                                    contentDescription = "Meeting title marker",
                                    modifier = Modifier
                                        .size(26.dp)
                                        .align(Alignment.Center)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(start = DP.small, end = DP.small)
        ) col3@{

            Spacer(modifier = Modifier.smallHeight())
            Divider(
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.smallHeight())

            // • FROM time / date
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
            ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = DP.medium, end = DP.medium)
                    ) {
                        Text(
                            "From",
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                        )
                        Text(
                            "8:00 AM",
                            textAlign = TextAlign.End,
                            modifier = Modifier
                        )
                    }
                    Text(
                        "Jul 21 2022",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                    )
            }
            Spacer(modifier = Modifier.smallHeight())
            Divider(
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.smallHeight())


            // • TO time / date
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = DP.medium, end = DP.medium)
                ) {
                    Text(
                        "To",
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                    )
                    Text(
                        "12:30 PM",
                        textAlign = TextAlign.End,
                        modifier = Modifier
                    )
                }
                Text(
                    "Aug 7 2022",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                )
            }
            Spacer(modifier = Modifier.smallHeight())
            Divider(
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.smallHeight())
        }

    }

}

@Preview
@Composable
fun preview() {
    TaskyTheme {
        AddEventScreenContent(
            state = AddEventState(),
            onAction = { println("ACTION: $it") },
            navigator = EmptyDestinationsNavigator,
        )
    }
}