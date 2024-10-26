package pl.vemu.zsme.ui

import android.Manifest
import android.app.assist.AssistContent
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
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
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.*
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.rememberNavHostEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.vemu.zsme.BuildConfig
import pl.vemu.zsme.R
import pl.vemu.zsme.remembers.Prefs
import pl.vemu.zsme.remembers.link
import pl.vemu.zsme.remembers.rememberStringPreference
import pl.vemu.zsme.ui.components.PrimaryScaffold
import pl.vemu.zsme.ui.components.Transitions
import pl.vemu.zsme.ui.theme.MainTheme
import soup.compose.material.motion.animation.rememberSlideDistance

val Context.dataStore by preferencesDataStore(name = "settings")
var fullScreen by mutableStateOf(false)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

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
            Transitions(slideDistance)
        }

        PrimaryScaffold(navController) {
            DestinationsNavHost(
                navGraph = NavGraphs.root,
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                defaultTransitions = transitions,
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
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isFlexibleUpdateAllowed && (appUpdateInfo.clientVersionStalenessDays
                ?: -1) < 7
        ) return

        manager.startUpdateFlowForResult(
            appUpdateInfo,
            this,
            AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE),
            0
        )
        manager.requestUpdateFlow().collect { appUpdateResult ->
            if (appUpdateResult is AppUpdateResult.Downloaded) appUpdateResult.completeUpdate()
        }
    }

    override fun onProvideAssistContent(outContent: AssistContent?) {
        super.onProvideAssistContent(outContent)
        outContent?.webUri = link?.toUri()
    }
}

/*TODO
* webview table full width
* timetable home widget
* hold splashscreen for initial loading
* shared element/bounds transition https://github.com/raamcosta/compose-destinations/releases/tag/2.1.0-beta02
* */
