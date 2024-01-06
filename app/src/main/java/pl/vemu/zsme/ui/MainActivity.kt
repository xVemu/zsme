package pl.vemu.zsme.ui

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.clientVersionStalenessDays
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.requestAppUpdateInfo
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.vemu.zsme.BuildConfig
import pl.vemu.zsme.R
import pl.vemu.zsme.remembers.Prefs
import pl.vemu.zsme.remembers.rememberStringPreference
import pl.vemu.zsme.ui.components.PrimaryScaffold
import pl.vemu.zsme.ui.components.transitions
import pl.vemu.zsme.ui.theme.MainTheme
import soup.compose.material.motion.animation.rememberSlideDistance

val Context.dataStore by preferencesDataStore(name = "settings")
var fullScreen by mutableStateOf(false)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Without this Android 34+ displays wrong theme, when launched with Android Studio.
        if (BuildConfig.DEBUG) setTheme(R.style.MainTheme)

        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        // Allows to change navbar color.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            val theme by rememberStringPreference(Prefs.THEME)
            MainTheme(if (theme == "system") isSystemInDarkTheme() else theme.toBoolean()) {
                Main()
            }
        }

        lifecycleScope.launch {
            launch {
                dataStore.data.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                    .map { it[Prefs.LANGUAGE.key] ?: Prefs.LANGUAGE.defaultValue }
                    .collectLatest { lang ->
                        Lingver.getInstance().apply {
                            if (lang == "system") return@collectLatest setFollowSystemLocale(this@MainActivity)

                            // fixes resetting language to system when it's first use of webview.
                            WebView(applicationContext).destroy()
                            setLocale(this@MainActivity, lang)
                        }
                    }
            }
            launch { setupRemoteConfig() }

            try {
                updateApp()
            } catch (_: Exception) {
            }
        }
        Firebase.messaging.subscribeToTopic("news")
        requestNotificationPermission()
    }

    @Composable
    private fun Main() {
        val navController = rememberNavController()

        val slideDistance = rememberSlideDistance()
        val transitions = remember(slideDistance) {
            transitions(slideDistance)
        }
        val navEngine = rememberNavHostEngine(rootDefaultAnimations = transitions)

        PrimaryScaffold(navController) {
            DestinationsNavHost(
                navGraph = NavGraphs.root,
                navController = navController,
                engine = navEngine,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    private suspend fun setupRemoteConfig() = withContext(Dispatchers.IO) {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        if (BuildConfig.DEBUG) remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
    }

    private suspend fun updateApp() {
        val manager = AppUpdateManagerFactory.create(applicationContext)
        val appUpdateInfo = manager.requestAppUpdateInfo()
        if (!(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isFlexibleUpdateAllowed && (appUpdateInfo.clientVersionStalenessDays
                ?: -1) >= 7)
        ) return
        manager.startUpdateFlowForResult(
            appUpdateInfo, AppUpdateType.FLEXIBLE, this, 0
        )
        manager.requestUpdateFlow().collect { appUpdateResult ->
            if (appUpdateResult is AppUpdateResult.Downloaded) appUpdateResult.completeUpdate()
        }
    }
}

/*TODO
* onProvideAssistContent
* formatter kotlin
* webview table full width
* dark theme
* */
