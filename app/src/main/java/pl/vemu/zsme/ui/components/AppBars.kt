@file:OptIn(ExperimentalMaterial3Api::class)

package pl.vemu.zsme.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import pl.vemu.zsme.R

/**
 * @param navController if null, no back button will be shown
 * */
@Composable
fun SimpleSmallAppBar(
    @StringRes title: Int,
    navController: DestinationsNavigator? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    SimpleSmallAppBar(stringResource(title), navController, scrollBehavior, actions)
}

/**
 * @param navController if null, no back button will be shown
 * */
@Composable
fun SimpleMediumAppBar(
    @StringRes title: Int,
    navController: DestinationsNavigator? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    SimpleMediumAppBar(stringResource(title), navController, scrollBehavior, actions)
}

/**
 * @param navController if null, no back button will be shown
 * */
@Composable
fun SimpleLargeAppBar(
    @StringRes title: Int,
    navController: DestinationsNavigator? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    SimpleLargeAppBar(stringResource(title), navController, scrollBehavior)
}

/**
 * @param navController if null, no back button will be shown
 * */
@Composable
fun SimpleSmallAppBar(
    title: String,
    navController: DestinationsNavigator? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = { AutoBackButton(navController) },
        scrollBehavior = scrollBehavior,
        actions = actions,
        windowInsets = windowInsetsWithoutStatusBar,
    )
}

/**
 * @param navController if null, no back button will be shown
 * */
@Composable
fun SimpleMediumAppBar(
    title: String,
    navController: DestinationsNavigator? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    MediumTopAppBar(
        title = { Text(title) },
        navigationIcon = { AutoBackButton(navController) },
        scrollBehavior = scrollBehavior,
        actions = actions,
        windowInsets = windowInsetsWithoutStatusBar,
    )
}

/**
 * @param navController if null, no back button will be shown
 * */
@Composable
fun SimpleLargeAppBar(
    title: String,
    navController: DestinationsNavigator? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    LargeTopAppBar(
        title = { Text(title) },
        navigationIcon = { AutoBackButton(navController) },
        scrollBehavior = scrollBehavior,
        windowInsets = windowInsetsWithoutStatusBar,
    )
}

@Composable
private fun AutoBackButton(navController: DestinationsNavigator? = null) {
    if (navController != null) IconButton(onClick = {
        navController.popBackStack()
    }) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(R.string.back_button),
        )
    }
}

private val windowInsetsWithoutStatusBar
    @Composable
    get() = TopAppBarDefaults.windowInsets.exclude(WindowInsets.statusBars)
