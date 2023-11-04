package pl.vemu.zsme.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import pl.vemu.zsme.R

@Composable
fun SimpleSmallAppBar(@StringRes title: Int, navController: NavController) {
    SimpleSmallAppBar(stringResource(title), navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleMediumAppBar(
    @StringRes title: Int, navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    SimpleMediumAppBar(stringResource(title), navController, scrollBehavior)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSmallAppBar(title: String, navController: NavController) {
    TopAppBar(title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleMediumAppBar(
    /*TODO automatically back button*/
    title: String,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    MediumTopAppBar(
        scrollBehavior = scrollBehavior,
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                )
            }
        }
    )
}
