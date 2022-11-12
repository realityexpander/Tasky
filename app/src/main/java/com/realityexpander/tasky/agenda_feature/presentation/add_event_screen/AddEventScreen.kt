package com.realityexpander.tasky.agenda_feature.presentation.add_event_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.agenda_feature.presentation.agenda_screen.AgendaEvent
import com.realityexpander.tasky.core.presentation.common.modifiers.*
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
//                }
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
                    .alignByBaseline()
                    .align(Alignment.CenterVertically)
            )

            // • TODAY'S DATE
            Text(
                "01 MARCH 2022",
                color = MaterialTheme.colors.surface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alignByBaseline()
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )

            Icon(
                imageVector = Icons.Filled.Edit,
                tint = MaterialTheme.colors.surface,
                contentDescription = "Edit Event",
                modifier = Modifier
                    .alignByBaseline()
                    .align(Alignment.CenterVertically)
                    .wrapContentWidth(Alignment.End)
            )

        }

        Spacer(modifier = Modifier.tinyHeight())

        // • HEADER FOR AGENDA ITEMS (S, M, T, W, T, F, & Day Picker)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
                .padding(0.dp)
        ) col2@{
            Spacer(modifier = Modifier.tinyHeight())

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(15.dp)
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