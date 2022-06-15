package pl.vemu.zsme.ui.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import pl.vemu.zsme.R
import pl.vemu.zsme.ui.components.simpleSmallAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Contact(navController: NavController) {
    val context = LocalContext.current
    Scaffold(
        topBar = simpleSmallAppBar(
            title = R.string.contact,
            navController = navController
        )
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(ContactItem.values()) { item ->
                Row {
                    Column(
                        Modifier
                            .weight(1F)
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
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun ContactPreview() {
    Contact(rememberNavController())
}