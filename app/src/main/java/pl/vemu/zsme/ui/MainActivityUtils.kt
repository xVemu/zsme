package pl.vemu.zsme.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DynamicFeed
import androidx.compose.material.icons.rounded.EventNote
import androidx.compose.material.icons.rounded.Subject
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.ramcosta.composedestinations.spec.NavGraphSpec
import pl.vemu.zsme.R

@Composable
fun NavController.currentScreenAsState(): State<NavGraphSpec> {
    val selectedItem: MutableState<NavGraphSpec> = remember { mutableStateOf(NavGraphs.post) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->

            selectedItem.value = destination.navGraph()
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedItem
}

private fun NavDestination.navGraph(): NavGraphSpec {
    hierarchy.forEach { destination ->
        NavGraphs.root.nestedNavGraphs.forEach { navGraph ->
            if (destination.route == navGraph.route) {
                return navGraph
            }
        }
    }

    throw RuntimeException("Unknown nav graph for destination $route")
}


enum class BottomNavItem(
    val destination: NavGraphSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    POST(NavGraphs.post, Icons.Rounded.DynamicFeed, R.string.post),
    TIMETABLE(NavGraphs.timetable, Icons.Rounded.EventNote, R.string.timetable),
    MORE(NavGraphs.more, Icons.Rounded.Subject, R.string.more);
}