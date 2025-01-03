package pl.vemu.zsme.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ReportGmailerrorred
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.vemu.zsme.R
import pl.vemu.zsme.capitalize
import pl.vemu.zsme.remembers.rememberConnectivityState

@Composable
fun CustomError(retry: (() -> Unit)? = null) {
    val hasNetwork = rememberConnectivityState(onAvailable = retry)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Rounded.ReportGmailerrorred,
            contentDescription = stringResource(R.string.error),
            modifier = Modifier.size(128.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(if (!hasNetwork) R.string.no_connection else R.string.error),
            style = MaterialTheme.typography.headlineSmall
        )
        retry?.let {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = it) {
                Text(text = stringResource(R.string.retry).capitalize())
            }
        }
    }
}

@Preview
@Composable
private fun CustomErrorPreview() {
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CustomError {}
        }
    }
}
