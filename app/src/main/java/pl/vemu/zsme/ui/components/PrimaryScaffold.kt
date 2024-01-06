package pl.vemu.zsme.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuite
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteType
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popBackStack
import pl.vemu.zsme.remembers.isLandscape
import pl.vemu.zsme.ui.BottomNavItem
import pl.vemu.zsme.ui.currentScreenAsState
import pl.vemu.zsme.ui.fullScreen

@OptIn(
    ExperimentalMaterial3AdaptiveNavigationSuiteApi::class,
)
@Composable
fun PrimaryScaffold(
    navController: NavController,
    content: @Composable BoxScope.() -> Unit,
) {

    Scaffold(contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.statusBars)) { padding ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        ) {
            NavigationSuiteScaffoldLayout(
                navigationSuite = {
                    AnimatedVisibility(
                        visible = !fullScreen,
                        enter = enterAnim(isLandscape),
                        exit = exitAnim(isLandscape)
                    ) {
                        Navigation(navController, isLandscape)
                    }
                },
                layoutType = if (isLandscape) NavigationSuiteType.NavigationRail else NavigationSuiteType.NavigationBar,
            ) {
                Box(
                    modifier = Modifier
                        .then(if (isLandscape) Modifier.displayCutoutPadding() else Modifier)
                        .consumeWindowInsets(padding),
                    content = content,
                )
            }
        }
    }
}

private fun enterAnim(rail: Boolean) =
    fadeIn() + if (rail) slideInHorizontally() else slideInVertically { it / 2 }

private fun exitAnim(rail: Boolean) =
    fadeOut() + if (rail) slideOutHorizontally() else slideOutVertically { it / 2 }

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
@Composable
private fun Navigation(navController: NavController, rail: Boolean) {
    val currentDestination by navController.currentScreenAsState()

    NavigationSuite(layoutType = if (rail) NavigationSuiteType.NavigationRail else NavigationSuiteType.NavigationBar) {
        BottomNavItem.entries.forEach { item ->
            val selected = currentDestination == item.destination

            item(label = { Text(stringResource(item.label)) }, selected = selected, icon = {
                Icon(
                    imageVector = if (selected) item.iconFilled else item.icon,
                    contentDescription = stringResource(item.label),
                )
            }, onClick = onClick@{
                if (selected) {
                    navController.popBackStack(
                        item.destination.startRoute,
                        false,
                    )
                    return@onClick
                }

                navController.navigate(item.destination) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            })
        }
    }
}

@Preview
@Composable
private fun BottomBarPreview() {
    val navController = rememberNavController()
    Navigation(
        navController = navController,
        rail = false,
    )
}

@Preview
@Composable
private fun NavigationRailPreview() {
    val navController = rememberNavController()
    Navigation(
        navController = navController,
        rail = true,
    )
}
