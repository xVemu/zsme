package pl.vemu.zsme.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import pl.vemu.zsme.R

@Composable
fun simpleSmallAppBar(@StringRes title: Int, navController: NavController): @Composable () -> Unit {
    return {
        SmallTopAppBar(
            title = { Text(stringResource(title)) },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                    )
                }
            }
        )
    }
}