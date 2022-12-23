package com.realityexpander.tasky.core.presentation.animatedTransitions

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.realityexpander.tasky.appDestination
import com.realityexpander.tasky.destinations.AgendaScreenDestination

@OptIn(ExperimentalAnimationApi::class)
object ScreenTransitions : DestinationStyle.Animated {

    val slideInHorizontally = slideInHorizontally(
        initialOffsetX = { 1000 },
        animationSpec = tween(500)
    )
    val slideInHorizontallyReverse = slideInHorizontally(
        initialOffsetX = { -1000 },
        animationSpec = tween(500)
    )

    val slideOutHorizontally = slideOutHorizontally(
        targetOffsetX = { 1000 },
        animationSpec = tween(500)
    )
    val slideOutHorizontallyReverse = slideOutHorizontally(
        targetOffsetX = { -1000 },
        animationSpec = tween(500)
    )

    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? {

        return when (initialState.appDestination()) {
            AgendaScreenDestination -> slideInHorizontally
            else -> slideInHorizontallyReverse
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? {

        return when (targetState.appDestination()) {
            AgendaScreenDestination -> slideOutHorizontally
            else -> slideOutHorizontallyReverse
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {

        return when (initialState.appDestination()) {
            AgendaScreenDestination -> slideInHorizontally
            else -> slideInHorizontallyReverse
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {

        return when (targetState.appDestination()) {
            AgendaScreenDestination -> slideOutHorizontally
            else -> slideOutHorizontallyReverse
        }
    }
}