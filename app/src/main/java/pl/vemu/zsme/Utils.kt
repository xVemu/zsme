package pl.vemu.zsme

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
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

fun Modifier.paddingStart(size: Dp) = this.padding(start = size)
fun Modifier.paddingTop(size: Dp) = this.padding(top = size)
fun Modifier.paddingEnd(size: Dp) = this.padding(end = size)
fun Modifier.paddingBottom(size: Dp) = this.padding(bottom = size)

fun Context.isNetworkAvailable() =
    (this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
        getNetworkCapabilities(activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } == true
    }

sealed interface Result<out T> {
    class Success<out T>(
        val value: T,
        val error: Exception? = null,
        val refreshing: Boolean = false,
    ) : Result<T> {
        operator fun component1() = value
        operator fun component2() = error
        operator fun component3() = refreshing
    }

    @JvmInline
    value class Failure(val error: Exception) : Result<Nothing> {
        operator fun component1() = error
    }

    data object Loading : Result<Nothing>
}

typealias ResultList<T> = Result<List<T>>

fun Context.launchCustomTabs(url: String) {
    try {
        CustomTabsIntent.Builder().build().launchUrl(this, url.toUri())
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, R.string.custom_tabs_error, Toast.LENGTH_SHORT).show()
    }
}

fun String.capitalize() = lowercase().replaceFirstChar { it.uppercase() }

fun PaddingValues.plus(top: Dp = 0.dp, left: Dp = 0.dp, right: Dp = 0.dp, bottom: Dp = 0.dp) =
    PaddingValues(
        top = calculateTopPadding() + top,
        start = calculateStartPadding(LayoutDirection.Ltr) + left,
        end = calculateEndPadding(LayoutDirection.Ltr) + right,
        bottom = calculateBottomPadding() + bottom,
    )
