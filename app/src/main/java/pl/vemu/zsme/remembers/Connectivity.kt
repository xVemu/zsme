package pl.vemu.zsme.remembers

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService

@Composable
fun rememberConnectivityState(onAvailable: (() -> Unit)? = null): Boolean {
    val context = LocalContext.current

    var state by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val connectivityManager =
            getSystemService(context, ConnectivityManager::class.java)


        if (connectivityManager == null) {
            state = false
            return@DisposableEffect onDispose { }
        }

        state = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (!state) onAvailable?.invoke()
                state = true
            }

            override fun onLost(network: Network) {
                state = false
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, callback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    return state
}
