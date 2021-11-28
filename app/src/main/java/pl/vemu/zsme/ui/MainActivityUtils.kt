package pl.vemu.zsme.ui

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DynamicFeed
import androidx.compose.material.icons.rounded.EventNote
import androidx.compose.material.icons.rounded.Subject
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import pl.vemu.zsme.R

@OptIn(ExperimentalAnimationApi::class)
object Transitions {
    val enterTransition: AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition? =
        { _, _ -> slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn() }
    val exitTransitionStartDestination: AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition? =
        { initial, target ->
            if (BottomNavItem.values()
                    .any { it.startDestination == initial.destination.route }
                && BottomNavItem.values()
                    .any { it.startDestination == target.destination.route }
            ) null
            else slideOutHorizontally() + fadeOut()
        }
    val popEnterTransitionStartDestination: AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition? =
        { initial, target ->
            if (BottomNavItem.values()
                    .any { it.startDestination == initial.destination.route }
                && BottomNavItem.values()
                    .any { it.startDestination == target.destination.route }
            ) null
            else slideInHorizontally() + fadeIn()
        }
    val popExitTransition: AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition? =
        { _, _ -> slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut() }
    val fadeInStartDestination: AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition? =
        { initial, target ->
            if (BottomNavItem.values()
                    .any { it.startDestination == initial.destination.route }
                && BottomNavItem.values()
                    .any { it.startDestination == target.destination.route }
            ) null
            else fadeIn()
        }
    val fadeOutStartDestination: AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition? =
        { initial, target ->
            if (BottomNavItem.values()
                    .any { it.startDestination == initial.destination.route }
                && BottomNavItem.values()
                    .any { it.startDestination == target.destination.route }
            ) null
            else fadeOut()
        }
}

enum class BottomNavItem(
    val route: String,
    val startDestination: String,
    val icon: ImageVector,
    @StringRes val title: Int
) {
    POST("postNav", "post", Icons.Rounded.DynamicFeed, R.string.post),
    TIMETABLE("timetableNav", "timetable", Icons.Rounded.EventNote, R.string.timetable),
    MORE("moreNav", "more", Icons.Rounded.Subject, R.string.more);
}