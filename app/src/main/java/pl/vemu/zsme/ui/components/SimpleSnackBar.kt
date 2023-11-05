package pl.vemu.zsme.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pl.vemu.zsme.R

/*TODO*/
@Preview
@Composable
private fun SimpleSnackBarPreview() {
    Box {
        val errorMsg = stringResource(R.string.error)
        val retryMsg = stringResource(R.string.retry)
        val snackbarData = object : SnackbarData {
            override val visuals = object : SnackbarVisuals {
                override val actionLabel: String = retryMsg
                override val duration: SnackbarDuration = SnackbarDuration.Indefinite
                override val message: String = errorMsg
                override val withDismissAction: Boolean = false
            }

            override fun dismiss() {}
            override fun performAction() {}
        }
        SnackBar(snackbarData)
    }
}

@Composable
fun BoxScope.SimpleSnackbar(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.align(Alignment.BottomCenter),
    ) { snackbarData ->
        SnackBar(snackbarData)
    }
}

@Composable
private fun SnackBar(snackbarData: SnackbarData) {
    Snackbar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        actionColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        snackbarData = snackbarData
    )
}

