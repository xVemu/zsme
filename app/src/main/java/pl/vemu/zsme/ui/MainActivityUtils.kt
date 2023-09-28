package pl.vemu.zsme.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.rounded.DynamicFeed
import androidx.compose.material.icons.rounded.EventNote
import androidx.compose.material.icons.rounded.Subject
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val iconFilled: ImageVector,
    @StringRes val label: Int
) {
    POST(NavGraphs.post, Icons.Rounded.DynamicFeed, Icons.Filled.DynamicFeed, R.string.post),
    TIMETABLE(
        NavGraphs.timetable,
        Icons.Rounded.EventNote,
        Icons.Filled.EventNote,
        R.string.timetable
    ),
    MORE(NavGraphs.more, Icons.Rounded.Subject, Icons.Filled.Subject, R.string.more);
}
