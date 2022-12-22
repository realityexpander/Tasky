package com.realityexpander.tasky.core.presentation.animatedTransitions

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.realityexpander.tasky.appDestination
import com.realityexpander.tasky.destinations.AgendaScreenDestination

@OptIn(ExperimentalAnimationApi::class)
object ScreenTransitions : DestinationStyle.Animated {

    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? {

        return when (initialState.appDestination()) {
            AgendaScreenDestination ->
                    slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(700)
                )
            else -> slideInHorizontally(
                initialOffsetX = { -1000 },
                animationSpec = tween(700)
            )
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? {

        return when (targetState.appDestination()) {
            AgendaScreenDestination ->
                    slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(700)
                )
            else -> slideOutHorizontally(
                targetOffsetX = { -1000 },
                animationSpec = tween(700)
            )
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {

        return when (initialState.appDestination()) {
            AgendaScreenDestination ->
                    slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(700)
                )
            else -> slideInHorizontally(
                initialOffsetX = { -1000 },
                animationSpec = tween(700)
            )
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {

        return when (targetState.appDestination()) {
            AgendaScreenDestination ->
                    slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(700)
                )
            else -> slideOutHorizontally(
                targetOffsetX = { -1000 },
                animationSpec = tween(700)
            )
        }
    }
}