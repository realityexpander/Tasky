package com.realityexpander.tasky

import android.content.Context
import android.os.Bundle
import android.os.Debug.waitForDebugger
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.dataStore
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.realityexpander.tasky.agenda_feature.data.repositories.TaskyDatabase
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.auth_feature.presentation.splash_screen.MainActivityViewModel
import com.realityexpander.tasky.core.data.settings.AppSettingsSerializer
import com.realityexpander.tasky.core.data.settings.saveSettingsInitialized
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.destinations.AgendaScreenDestination
import com.realityexpander.tasky.destinations.LoginScreenDestination
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime
import kotlin.system.exitProcess

val Context.dataStore by
    dataStore(
        "app-settings.data",
        AppSettingsSerializer(encrypted = true)
    )

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        if(false) {
            waitForDebugger() // leave for testing process death
        }

        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.splashState.value.isLoading
            }
        }

        setContent {
            TaskyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.tasky_green)
                ) {
                    val splashState by viewModel.splashState.collectAsState()
                    val context = LocalContext.current

                    val navController = rememberNavController()
                    val navHostEngine = rememberNavHostEngine()

                    // Load Settings (or initialize them)
                    LaunchedEffect(true) {
                        val appSettings = context.dataStore.data.first()

                        // Confirm the settings file is created and initialized
                        if (!appSettings.isSettingsInitialized) {
                            context.dataStore.saveSettingsInitialized(true)
                        }

                        // Set user logged-in status
                        viewModel.onSetAuthInfo(appSettings.authInfo)
                    }

                    if (!splashState.isLoading) {

                        // Check for errors
                        if(splashState.error != null) {
                            Toast.makeText(context, splashState.error, Toast.LENGTH_LONG).show()
                            Thread.sleep(2000)
                            viewModel.onSetAuthInfo(null)
                        }

                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController,
                            engine = navHostEngine,
                            startRoute =
                                if (splashState.authInfo != null) {
                                    AgendaScreenDestination
                                } else {
                                    LoginScreenDestination
                                },
                        )
                    }
                }
            }
        }
    }

    fun exitApp() {
        finish()
        exitProcess(0)
    }
}

///////////////////////////////////////////////////////////////////////////////////
// Local Testing

@OptIn(DelicateCoroutinesApi::class)
fun test_db(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)) {


    // create a new database
    val taskyDB = Room.inMemoryDatabaseBuilder(
        context,
        TaskyDatabase::class.java
    ).build()
    val db = taskyDB.eventDao()

    // Test Database Flow
    runBlocking {

        scope.launch {
            db.getEventsFlow().collect {
                println("EntityDBTable Flow: ${it.map { it.title }}")
            }
        }

        db.createEvent(
            EventEntity(
                "1",
                "Event 1",
                "2021-01-01T00:00:00.000Z",
                from = ZonedDateTime.now(),
                to = ZonedDateTime.now(),
                remindAt = ZonedDateTime.now(),
                host = "Host 1",
                isUserEventCreator = true,
                isGoing = true,
                attendeeIds = listOf("1", "2", "3"),
                photos = listOf("photo1", "photo2", "photo3"),
                deletedPhotoKeys = listOf(),
                isDeleted = false,
            )
        )

        delay(400)

        db.createEvent(
            EventEntity(
                "2",
                "Event 2",
                "2021-01-01T00:00:00.000Z",
                from = ZonedDateTime.now(),
                to = ZonedDateTime.now(),
                remindAt = ZonedDateTime.now(),
                host = "Host 2",
                isUserEventCreator = true,
                isGoing = true,
                attendeeIds = listOf("1", "2", "3"),
                photos = listOf("photo1", "photo2", "photo3"),
                deletedPhotoKeys = listOf(),
                isDeleted = false,
            )
        )

        db.createEvent(
            EventEntity(
                "3",
                "Event 3",
                "2021-01-01T00:00:00.000Z",
                from = ZonedDateTime.now(),
                to = ZonedDateTime.now(),
                remindAt = ZonedDateTime.now(),
                host = "Host 3",
                isUserEventCreator = true,
                isGoing = true,
                attendeeIds = listOf("1", "2", "3"),
                photos = listOf("photo1", "photo2", "photo3"),
                deletedPhotoKeys = listOf(),
                isDeleted = false,
            )
        )

        delay(100)

        db.createEvent(
            EventEntity(
                "4",
                "Event 4",
                "2021-01-01T00:00:00.000Z",
                from = ZonedDateTime.now(),
                to = ZonedDateTime.now(),
                remindAt = ZonedDateTime.now(),
                host = "Host 4",
                isUserEventCreator = true,
                isGoing = true,
                attendeeIds = listOf("1", "2", "3"),
                photos = listOf("photo1", "photo2", "photo3"),
                deletedPhotoKeys = listOf(),
                isDeleted = false,
            )
        )

        val eventsForDay = db.getEventsForDay(ZonedDateTime.now())
        println("Events for day: ${eventsForDay.map { it.title }}")

        delay(100)
        println()

        print(".updateEvent(eventId=4) -> ")
        db.updateEvent(
            EventEntity(
                "4",
                "Event 4 - updated",
                "Description updated",
                from = ZonedDateTime.now(),
                to = ZonedDateTime.now(),
                remindAt = ZonedDateTime.now(),
                host = "Host 4",
                isUserEventCreator = true,
                isGoing = true,
                attendeeIds = listOf("1", "2", "3"),
                photos = listOf("photo1", "photo2", "photo3"),
                deletedPhotoKeys = listOf(),
                isDeleted = false,
            )
        )

        delay(100)
        println()

        print(".deleteEventById(eventId=4) -> ")
        db.markEventDeletedById("4")

        val deletedEventIds =
            db.getMarkedDeletedEventIds().also { eventIds ->
                println("EventIds Marked as Deleted: $eventIds")
            }

        delay(100)
        println()

        print(".deleteFinallyByEventIds(deletedEventIds) -> ")
        db.deleteFinallyByEventIds(deletedEventIds)

        delay(100)

        db.getMarkedDeletedEventIds().also { eventIds ->
            println("EventIds Marked as Deleted: $eventIds")
        }

        println()
        print(".clearAllEvents() -> ")
        db.clearAllEvents()

        delay(2000)

    }
}