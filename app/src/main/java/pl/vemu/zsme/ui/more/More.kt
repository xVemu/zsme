package pl.vemu.zsme.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun More(navController: NavController) {
    val context = LocalContext.current
    LazyColumn {
        items(MoreItem.values()) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        item.onClick(context, navController)
                    }
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = stringResource(id = item.text),
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = stringResource(id = item.text),
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}