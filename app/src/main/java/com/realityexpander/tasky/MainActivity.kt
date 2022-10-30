package com.realityexpander.tasky

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.datastore.dataStore
import com.ramcosta.composedestinations.DestinationsNavHost
import com.realityexpander.tasky.common.settings.AppSettingsSerializer
import com.realityexpander.tasky.presentation.NavGraphs
import com.realityexpander.tasky.presentation.ui.theme.TaskyTheme
import dagger.hilt.android.AndroidEntryPoint

val Context.dataStore by dataStore("app-settings.json", AppSettingsSerializer)

@AndroidEntryPoint
class MainActivity() : ComponentActivity() {
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