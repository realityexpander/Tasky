package com.realityexpander.tasky.core.util.InternetConnectivityObserver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.realityexpander.observeconnectivity.IInternetConnectivityObserver
import com.realityexpander.tasky.R
import kotlinx.coroutines.delay

@Composable
fun InternetAvailabilityIndicator(connectivityState: IInternetConnectivityObserver.OnlineStatus) {
    // Offline? Show delayed warning in case starting up
    val isOfflineBannerVisible = remember { mutableStateOf(false) }
    LaunchedEffect(connectivityState) {
        isOfflineBannerVisible.value = false

        if (connectivityState == IInternetConnectivityObserver.OnlineStatus.OFFLINE
        ) {
            delay(1000)
            isOfflineBannerVisible.value = true
        }
    }
    AnimatedVisibility(isOfflineBannerVisible.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            Text(
                text = stringResource(R.string.no_internet),
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(Color.Red.copy(alpha = .5f))
                    .fillMaxWidth()
                    .align(Alignment.Center),

                )
        }
    }
}