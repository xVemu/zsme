package pl.vemu.zsme.ui.components

import androidx.compose.animation.*
import androidx.compose.ui.Alignment
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle

@OptIn(ExperimentalAnimationApi::class)
object SlideTransition : DestinationStyle.Animated {

    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? =
        makeTransition(slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn())

    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? =
        makeTransition(slideOutHorizontally() + fadeOut())

    override fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? =
        makeTransition(slideInHorizontally() + fadeIn())

    override fun AnimatedContentScope<NavBackStackEntry>.popExitTransition(): ExitTransition? =
        makeTransition(slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut())
}

@OptIn(ExperimentalAnimationApi::class)
object ExpandTransition : DestinationStyle.Animated {

    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? =
        makeTransition(expandIn(expandFrom = Alignment.Center) + fadeIn())

    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? =
        makeTransition(fadeOut())

    override fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? =
        makeTransition(fadeIn())

    override fun AnimatedContentScope<NavBackStackEntry>.popExitTransition(): ExitTransition? =
        makeTransition(shrinkOut(shrinkTowards = Alignment.Center) + fadeOut())
}

@OptIn(ExperimentalAnimationApi::class)
private fun <T> AnimatedContentScope<NavBackStackEntry>.makeTransition(transition: T): T? =
    if (initialState.destination.parent?.startDestinationRoute ==
        targetState.destination.parent?.startDestinationRoute
    ) transition
    else null