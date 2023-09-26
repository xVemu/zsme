package pl.vemu.zsme.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.Alignment
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle


object SlideTransition : DestinationStyle.Animated {

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition? =
        makeTransition(slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn())

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition? =
        makeTransition(slideOutHorizontally() + fadeOut())

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? =
        makeTransition(slideInHorizontally() + fadeIn())

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition? =
        makeTransition(slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut())
}


object ExpandTransition : DestinationStyle.Animated {

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition? =
        makeTransition(expandIn(expandFrom = Alignment.Center) + fadeIn())

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition? =
        makeTransition(fadeOut())

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? =
        makeTransition(fadeIn())

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition? =
        makeTransition(shrinkOut(shrinkTowards = Alignment.Center) + fadeOut())
}

private fun <T> AnimatedContentTransitionScope<NavBackStackEntry>.makeTransition(transition: T): T? =
    if (initialState.destination.parent?.startDestinationRoute ==
        targetState.destination.parent?.startDestinationRoute
    ) transition
    else null
