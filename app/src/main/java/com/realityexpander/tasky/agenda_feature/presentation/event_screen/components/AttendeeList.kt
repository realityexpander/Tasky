package com.realityexpander.tasky.agenda_feature.presentation.event_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.presentation.common.components.UserAcronymCircle
import com.realityexpander.tasky.core.presentation.common.modifiers.smallHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.smallWidth
import com.realityexpander.tasky.core.presentation.common.modifiers.xxSmallHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.xxSmallWidth
import com.realityexpander.tasky.core.util.UserId

@Composable
fun AttendeeList(
    loggedInUserId: UserId,
    isUserEventCreator: Boolean,
    header: String,
    attendees: List<Attendee>,
    onAttendeeClick: (Attendee) -> Unit,
    onAttendeeRemoveClick: (Attendee) -> Unit,
    hostUserId: UserId,
) {
    // • Header
    Text(
        header,
        color = MaterialTheme.colors.onSurface,
        style = MaterialTheme.typography.h4,
        fontWeight = FontWeight.Normal,
        modifier = Modifier
    )
    Spacer(modifier = Modifier.smallHeight())


    // • Attendee list
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        attendees.forEach { attendee ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colors.onSurface.copy(alpha = .1f))
                    .padding(top = 2.dp, bottom = 2.dp)
            ) {
                Row {
                    Spacer(modifier = Modifier.smallWidth())

                    UserAcronymCircle(
                        username = attendee.fullName,
                        color = MaterialTheme.colors.surface,
                        circleBackgroundColor = MaterialTheme.colors.onSurface.copy(
                            alpha = .25f
                        ),
                        modifier = Modifier
                            .alignByBaseline()
                    )
                    Spacer(modifier = Modifier.xxSmallWidth())

                    Text(
                        attendee.fullName,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .alignByBaseline()
                    )
                }

                // • Remove Attendee
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {

                    // • Creator cant remove himself, only other attendees.
                    if( (attendee.id == loggedInUserId && isUserEventCreator)
                        || attendee.id == hostUserId
                    ) {
                        Text(
                            "creator",
                            color = MaterialTheme.colors.onSurface.copy(alpha = .3f)
                        )
                    } else if (isUserEventCreator) {  // • Creator can remove other attendees.
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            tint = MaterialTheme.colors.onSurface,
                            contentDescription = "Attendee remove",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    onAttendeeRemoveClick(attendee)
                                }
                        )
                    }
                    Spacer(modifier = Modifier.smallWidth())
                }

            }
            Spacer(modifier = Modifier.xxSmallHeight())
        }
    }
}