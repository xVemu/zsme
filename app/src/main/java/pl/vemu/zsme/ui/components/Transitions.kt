package pl.vemu.zsme.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut

fun transitions(slideDistance: Int) = RootNavGraphDefaultAnimations(
    enterTransition = {
        if (isSameNavGraph) materialSharedAxisXIn(true, slideDistance)
        else materialFadeThroughIn()
    },
    exitTransition = {
        if (isSameNavGraph) materialSharedAxisXOut(true, slideDistance)
        else materialFadeThroughOut()
    },
    popEnterTransition = {
        if (isSameNavGraph) materialSharedAxisXIn(false, slideDistance)
        else materialFadeThroughIn()
    },
    popExitTransition = {
        if (isSameNavGraph) materialSharedAxisXOut(false, slideDistance)
        else materialFadeThroughOut()
    }
)

private val AnimatedContentTransitionScope<NavBackStackEntry>.isSameNavGraph
    get() = initialState.destination.parent?.startDestinationRoute == targetState.destination.parent?.startDestinationRoute
