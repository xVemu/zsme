package pl.vemu.zsme.ui.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.vemu.zsme.R

@Composable
fun Contact() {
    val context = LocalContext.current
    return LazyColumn {
        items(ContactItem.values()) { item ->
            Row {
                Column(
                    modifier = Modifier
                        .weight(1F)
                        .padding(8.dp)
                ) {
                    Text(
                        text = stringResource(id = item.headerText),
                        style = MaterialTheme.typography.labelLarge
                    )
                    SelectionContainer {
                        Text(
                            text = stringResource(id = item.text),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                item.icon?.let {
                    IconButton(
                        onClick = { item.onClick(context) },
                    ) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = stringResource(R.string.destination_button),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}