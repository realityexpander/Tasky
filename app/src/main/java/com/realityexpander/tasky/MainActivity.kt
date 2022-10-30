package com.realityexpander.tasky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.destinations.AgendaScreenDestination
import com.realityexpander.tasky.destinations.LoginScreenDestination
import com.realityexpander.tasky.presentation.ui.theme.TaskyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

//        waitForDebugger() // for testing process death

        super.onCreate(savedInstanceState)

        setContent {
            TaskyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.tasky_green)
                ) {
                    DestinationsNavHost(navGraph = NavGraphs.root)
                }

            }
        }
    }
}

@Composable
@Destination
@RootNavGraph(start = true)
fun SplashScreen(
    navigator: DestinationsNavigator,
) {
    LaunchedEffect(key1 = true) {
        delay(2000) // simulate loading/validating user session
        val isLoggedIn = false

        // TESTING ONLY
        if (isLoggedIn) {
            navigator.navigate(AgendaScreenDestination())
        } else {
            navigator.navigate(
                LoginScreenDestination(
                    username = "Chris Athanas",     // TESTING ONLY
                    email = "chris@demo.com",       // TESTING ONLY
                    password = "Password1",         // TESTING ONLY
                    confirmPassword = "Password1",  // TESTING ONLY
                )
            ) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.tasky_green)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.tasky_logo),
            contentDescription = "Tasky Logo",
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
                .offset { IntOffset(0, -10) } // slight difference between Android Theme and this composable when centering
        )
    }

}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    TaskyTheme {
        SplashScreen(navigator = EmptyDestinationsNavigator)
    }
}