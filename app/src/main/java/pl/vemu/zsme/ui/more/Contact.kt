package pl.vemu.zsme.ui.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import pl.vemu.zsme.DEFAULT_URL
import pl.vemu.zsme.R
import pl.vemu.zsme.ui.components.SimpleMediumAppBar
import pl.vemu.zsme.ui.components.SlideTransition

@MoreNavGraph
@Destination(
    route = "more/contact",
    deepLinks = [DeepLink(uriPattern = "$DEFAULT_URL/wp/kontakt/")],
    style = SlideTransition::class
)
@Composable
fun Contact(navController: NavController) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            SimpleMediumAppBar(
                title = R.string.contact,
                navController = navController
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ContactItem.entries.forEach { item ->
                Column(
                    Modifier
                        .padding(8.dp)
                ) {
                    Text(
                        text = stringResource(item.headerText),
                        style = MaterialTheme.typography.labelLarge
                    )
                    SelectionContainer {
                        Text(
                            text = stringResource(item.text),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                item.icon?.let {
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
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun ContactPreview() {
    Contact(rememberNavController())
}
