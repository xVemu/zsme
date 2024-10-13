package pl.vemu.zsme.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.animations.NavHostAnimatedDestinationStyle
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut

class Transitions(private val slideDistance: Int) : NavHostAnimatedDestinationStyle() {
    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        if (isSameNavGraph) materialSharedAxisXIn(true, slideDistance)
        else materialFadeThroughIn()
    }
    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        if (isSameNavGraph) materialSharedAxisXOut(true, slideDistance)
        else materialFadeThroughOut()
    }

    override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        if (isSameNavGraph) materialSharedAxisXIn(false, slideDistance)
        else materialFadeThroughIn()
    }
    override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        if (isSameNavGraph) materialSharedAxisXOut(false, slideDistance)
        else materialFadeThroughOut()
    }
}

private val AnimatedContentTransitionScope<NavBackStackEntry>.isSameNavGraph
    get() = initialState.destination.parent?.startDestinationRoute == targetState.destination.parent?.startDestinationRoute
