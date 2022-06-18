package pl.vemu.zsme.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import pl.vemu.zsme.R
import pl.vemu.zsme.ui.components.SlideTransition

@RootNavGraph
@NavGraph
annotation class MoreNavGraph(
    val start: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@MoreNavGraph(start = true)
@Destination(
    route = "more/main",
    deepLinks = [DeepLink(uriPattern = "zsme://more")],
    style = SlideTransition::class
)
@Composable
fun More(navController: NavController) {
    val context = LocalContext.current
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text(stringResource(R.string.more)) })
    }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            items(MoreItem.values()) { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { item.onClick(context, navController) }
                        .padding(16.dp)) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.text),
                    )
                    Spacer(Modifier.width(32.dp))
                    Text(
                        text = stringResource(item.text),
                        modifier = Modifier.align(Alignment.CenterVertically),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun MorePreview() {
    More(navController = rememberNavController())
}