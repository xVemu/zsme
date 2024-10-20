package pl.vemu.zsme.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.ramcosta.composedestinations.spec.NavGraphSpec
import pl.vemu.zsme.ui.BottomNavItem
import pl.vemu.zsme.ui.currentScreenAsState
import pl.vemu.zsme.ui.fullScreen
import pl.vemu.zsme.util.changeUiVisibility
import pl.vemu.zsme.util.rememberInsetsController

@Composable
fun PrimaryScaffold(
    navController: NavController,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        color = NavigationSuiteScaffoldDefaults.containerColor,
        contentColor = NavigationSuiteScaffoldDefaults.contentColor
    ) {
        val layoutType = currentWindowAdaptiveInfo().calculateType()

        val systemUi = rememberInsetsController()
        LaunchedEffect(fullScreen) {
            systemUi?.changeUiVisibility(!fullScreen)
        }

        NavigationSuiteScaffoldLayout(
            navigationSuite = {
                AnimatedVisibility(
                    visible = !fullScreen,
                    enter = enterAnim(layoutType != NavigationSuiteType.NavigationBar),
                    exit = exitAnim(layoutType != NavigationSuiteType.NavigationBar)
                ) {
                    val currentDestination by navController.currentScreenAsState()

                    NavigationSuite(
                        layoutType = layoutType,
                        content = {
                            items(navController, currentDestination)
                        }
                    )
                }
            },
            layoutType = layoutType,
            content = {
                Box(
                    Modifier.consumeWindowInsets(
                        when (layoutType) {
                            NavigationSuiteType.NavigationBar ->
                                NavigationBarDefaults.windowInsets.only(WindowInsetsSides.Bottom)

                            NavigationSuiteType.NavigationRail ->
                                NavigationRailDefaults.windowInsets.only(WindowInsetsSides.Start)

                            NavigationSuiteType.NavigationDrawer ->
                                DrawerDefaults.windowInsets.only(WindowInsetsSides.Start)

                            else -> WindowInsets(0, 0, 0, 0)
                        }
                    )
                ) {
                    content()
                }
            }
        )
    }
}

private fun enterAnim(rail: Boolean) =
    fadeIn() + if (rail) slideInHorizontally() else slideInVertically { it / 2 }

private fun exitAnim(rail: Boolean) =
    fadeOut() + if (rail) slideOutHorizontally() else slideOutVertically { it / 2 }


private fun NavigationSuiteScope.items(navController: NavController, currentDestination: NavGraphSpec) {
    BottomNavItem.entries.forEach { item ->
        val selected = currentDestination == item.destination

        item(
            label = { Text(stringResource(item.label)) }, selected = selected,
            icon = {
                Icon(
                    imageVector = if (selected) item.iconFilled else item.icon,
                    contentDescription = stringResource(item.label),
                )
            },
            onClick = onClick@{
                if (selected) {
                    navController.popBackStack(
                        item.destination.startRoute.route,
                        false,
                    )
                    return@onClick
                }

                navController.navigate(item.destination.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
        )
    }
}

private fun WindowAdaptiveInfo.calculateType(): NavigationSuiteType {
    val height = windowSizeClass.windowHeightSizeClass
    val width = windowSizeClass.windowWidthSizeClass

    return when {
        windowPosture.isTabletop -> NavigationSuiteType.NavigationBar
        height == WindowHeightSizeClass.COMPACT || width == WindowWidthSizeClass.MEDIUM || width == WindowWidthSizeClass.EXPANDED -> NavigationSuiteType.NavigationRail
        else -> NavigationSuiteType.NavigationBar
    }
}
