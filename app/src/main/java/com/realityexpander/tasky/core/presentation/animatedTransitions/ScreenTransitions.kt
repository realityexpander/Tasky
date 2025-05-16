package com.realityexpander.tasky.core.presentation.animatedTransitions

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle.*
import com.realityexpander.tasky.appDestination
import com.realityexpander.tasky.core.presentation.animatedTransitions.ScreenTransitions.slideInHorizontallyReverse
import com.realityexpander.tasky.core.presentation.animatedTransitions.ScreenTransitions.slideOutHorizontallyReverse
import com.realityexpander.tasky.destinations.AgendaScreenDestination

@OptIn(ExperimentalAnimationApi::class)

object ScreenTransitions : Animated {

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

    // CDA FIX
//     fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? {
     fun AnimatedContentScope.enterTransition(): EnterTransition? {

//        return when (initialState.appDestination()) {
//            AgendaScreenDestination -> slideInHorizontally
//            else -> slideInHorizontallyReverse
//        }
        return slideInHorizontally
    }

//     fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? {
     fun AnimatedContentScope.exitTransition(): ExitTransition? {

//        return when (targetState.appDestination()) {
//            AgendaScreenDestination -> slideOutHorizontally
//            else -> slideOutHorizontallyReverse
//        }
        return slideOutHorizontally
    }

//     fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {
     fun AnimatedContentScope.popEnterTransition(): EnterTransition? {

//        return when (initialState.appDestination()) {
//            AgendaScreenDestination -> slideInHorizontally
//            else -> slideInHorizontallyReverse
//        }
        return slideInHorizontally
    }

//     fun AnimatedContentScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {
     fun AnimatedContentScope.popExitTransition(): ExitTransition? {

//        return when (targetState.appDestination()) {
//            AgendaScreenDestination -> slideOutHorizontally
//            else -> slideOutHorizontallyReverse
//        }
        return slideOutHorizontally
    }
}
