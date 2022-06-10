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
    val enterTransition =
        makeTransition(slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn())
    val exitTransition =
        makeTransition(slideOutHorizontally() + fadeOut())
    val popEnterTransition =
        makeTransition(slideInHorizontally() + fadeIn())
    val popExitTransition =
        makeTransition(slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut())
    val fadeIn =
        makeTransition(fadeIn())
    val fadeOut =
        makeTransition(fadeOut())

    private fun <T> makeTransition(transition: T):
            AnimatedContentScope<NavBackStackEntry>.() -> T? =
        {
            if (initialState.destination.parent?.startDestinationRoute ==
                targetState.destination.parent?.startDestinationRoute
            ) transition
            else null
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