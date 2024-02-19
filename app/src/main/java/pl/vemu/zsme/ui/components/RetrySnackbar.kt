package pl.vemu.zsme.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.vemu.zsme.R
import pl.vemu.zsme.remembers.rememberConnectivityState

@Composable
fun BoxScope.RetrySnackbar(
    modifier: Modifier = Modifier,
    retry: () -> Unit,
) {
    val hasNetwork = rememberConnectivityState(retry)

    Snackbar(
        modifier = modifier
            .align(Alignment.BottomCenter),
        snackbarData = object : SnackbarData {
            override val visuals = object : SnackbarVisuals {
                override val actionLabel = stringResource(R.string.retry)
                override val duration = SnackbarDuration.Indefinite
                override val message =
                    stringResource(if (!hasNetwork) R.string.no_connection else R.string.error)
                override val withDismissAction = false
            }

            override fun dismiss() = Unit

            override fun performAction() {
                retry()
            }
        },
    )
}
