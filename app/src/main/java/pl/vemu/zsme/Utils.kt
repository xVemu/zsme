package pl.vemu.zsme

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ln

fun Modifier.paddingStart(size: Dp) = this.padding(size, 0.dp, 0.dp, 0.dp)
fun Modifier.paddingTop(size: Dp) = this.padding(0.dp, size, 0.dp, 0.dp)
fun Modifier.paddingEnd(size: Dp) = this.padding(0.dp, 0.dp, size, 0.dp)
fun Modifier.paddingBottom(size: Dp) = this.padding(0.dp, 0.dp, 0.dp, size)

fun ColorScheme.surfaceColorWithElevation(
    elevation: Dp,
) = primary.copy(alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f).compositeOver(surface)

fun Context.isNetworkAvailable() =
    (this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
        getNetworkCapabilities(activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }

const val DEFAULT_URL = "https://zsme.tarnow.pl"

sealed class Result<out T> {
    class Success<out T>(val value: T) : Result<T>()
    class Failure(val error: Exception) : Result<Nothing>()
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