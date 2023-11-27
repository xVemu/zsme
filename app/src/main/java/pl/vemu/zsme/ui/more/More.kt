package pl.vemu.zsme.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import pl.vemu.zsme.R
import pl.vemu.zsme.ui.components.SimpleLargeAppBar


@RootNavGraph
@NavGraph
annotation class MoreNavGraph(
    val start: Boolean = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@MoreNavGraph(start = true)
@Destination(
    route = "more/main",
    deepLinks = [DeepLink(uriPattern = "zsme://more")],
)
@Composable
fun More(navController: DestinationsNavigator) {
    Scaffold(topBar = { SimpleLargeAppBar(R.string.more) }) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            val context = LocalContext.current
            MoreItem.entries.forEach { item ->
                ListItem(modifier = Modifier.clickable { item.onClick(context, navController) },
                    headlineContent = {
                        Text(stringResource(item.text))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(item.text),
                        )
                    })
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun MorePreview() {
    More(EmptyDestinationsNavigator)
}
