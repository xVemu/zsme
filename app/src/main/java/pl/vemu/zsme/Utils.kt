package pl.vemu.zsme

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

fun Modifier.paddingStart(size: Dp) = this.padding(size, 0.dp, 0.dp, 0.dp)
fun Modifier.paddingTop(size: Dp) = this.padding(0.dp, size, 0.dp, 0.dp)
fun Modifier.paddingEnd(size: Dp) = this.padding(0.dp, 0.dp, size, 0.dp)
fun Modifier.paddingBottom(size: Dp) = this.padding(0.dp, 0.dp, 0.dp, size)

fun Context.isNetworkAvailable() =
    (this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
        getNetworkCapabilities(activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }

sealed interface Result<out T> {
    @JvmInline
    value class Success<out T>(val value: T) : Result<T> {
        operator fun component1() = value
    }

    @JvmInline
    value class Failure(val error: Exception) : Result<Nothing> {
        operator fun component1() = error
    }

    data object Loading : Result<Nothing>
}

typealias ResultList<T> = Result<List<T>>

fun Context.launchCustomTabs(url: String) {
    CustomTabsIntent.Builder().build().launchUrl(this, url.toUri())
}

fun String.capitalize() = lowercase().replaceFirstChar { it.uppercase() }

fun PaddingValues.plus(top: Dp = 0.dp, left: Dp = 0.dp, right: Dp = 0.dp, bottom: Dp = 0.dp) =
    PaddingValues(
        top = calculateTopPadding() + top,
        start = calculateStartPadding(LayoutDirection.Ltr) + left,
        end = calculateEndPadding(LayoutDirection.Ltr) + right,
        bottom = calculateBottomPadding() + bottom,
    )
