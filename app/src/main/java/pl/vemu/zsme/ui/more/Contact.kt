package pl.vemu.zsme.ui.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import pl.vemu.zsme.DEFAULT_URL
import pl.vemu.zsme.R
import pl.vemu.zsme.ui.components.SimpleMediumAppBar
import pl.vemu.zsme.ui.components.SlideTransition

@OptIn(ExperimentalMaterial3Api::class)
@MoreNavGraph
@Destination(
    route = "more/contact",
    deepLinks = [DeepLink(uriPattern = "$DEFAULT_URL/wp/kontakt/")],
    style = SlideTransition::class
)
@Composable
fun Contact(navController: DestinationsNavigator) {
    val context = LocalContext.current
    val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(behavior.nestedScrollConnection),
        topBar = {
            SimpleMediumAppBar(
                title = R.string.contact, navController = navController, scrollBehavior = behavior,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(padding)
        ) {
            ContactItem.entries.forEach { item ->
                ListItem(
                    headlineContent = { Text(stringResource(item.text)) },
                    overlineContent = { Text(stringResource(item.headerText)) },
                    trailingContent = item.icon?.let {
                        {
                            IconButton(
                                onClick = { item.onClick(context) },
                            ) {
                                Icon(
                                    painter = painterResource(it),
                                    contentDescription = stringResource(R.string.destination_button),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun ContactPreview() {
    Contact(EmptyDestinationsNavigator)
}
