package pl.vemu.zsme.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.annotation.parameters.DeepLink
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import pl.vemu.zsme.R
import pl.vemu.zsme.ui.components.SimpleLargeAppBar

@NavGraph<RootGraph>
annotation class MoreNavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Destination<MoreNavGraph>(
    route = "more/main",
    start = true,
    deepLinks = [DeepLink("pl.vemu.zsme.shortcut.MORE")],
)
@Composable
fun More(navController: DestinationsNavigator) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = { SimpleLargeAppBar(R.string.more, scrollBehavior = scrollBehavior) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding),
        ) {
            val context = LocalContext.current
            MoreItem.entries.forEach { item ->
                ListItem(
                    modifier = Modifier.clickable { item.onClick(context, navController) },
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
