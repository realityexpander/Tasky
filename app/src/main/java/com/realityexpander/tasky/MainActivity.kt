package com.realityexpander.tasky

import android.os.Bundle
import android.os.Debug.waitForDebugger
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.realityexpander.tasky.destinations.LoginScreenDestination
import com.realityexpander.tasky.destinations.RegisterScreenDestination
import com.realityexpander.tasky.presentation.ui.theme.TaskyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

//        waitForDebugger() // for testing process death

        super.onCreate(savedInstanceState)

        setContent {
            TaskyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.green_tasky)
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
        //delay(2000)

        navigator.navigate(
            LoginScreenDestination()
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.green_tasky)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.tasky_logo),
            contentDescription = "Tasky Logo",
            modifier = Modifier
                .width(250.dp)
                .height(250.dp)
                .offset { IntOffset(0, -10) } // slight difference between Android Theme and this composable
        )
    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TaskyTheme {
    }
}