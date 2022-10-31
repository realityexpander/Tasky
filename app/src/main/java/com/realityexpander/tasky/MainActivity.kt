package com.realityexpander.tasky

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.DestinationsNavHost
import com.realityexpander.tasky.core.common.settings.AppSettingsSerializer
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

val Context.dataStore by dataStore("app-settings.json", AppSettingsSerializer)

@AndroidEntryPoint
class MainActivity() : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

//        waitForDebugger() // for testing process death

        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }

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

    fun exitApp() {
        finish()
        exitProcess(0)
    }
}

class MainViewModel: ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            //delay(1500)
            _isLoading.value = false
        }
    }
}

// to scale the splash screen if XML/SVG:
// put <path> inside a group as so:
// <group
//   android:scaleX="0.5"
//   android:scaleY="0.5"
//   android:pivotX="<half viewportWidth>"   // could also use half the width of the viewportWidth
//   android:pivotY="<half viewportHeight>"   // could also use half the height of the viewportHeight
//   >
//   <path ... />
// </group>