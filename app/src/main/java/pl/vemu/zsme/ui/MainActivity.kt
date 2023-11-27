package pl.vemu.zsme.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.clientVersionStalenessDays
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.requestAppUpdateInfo
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popBackStack
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pl.vemu.zsme.BuildConfig
import pl.vemu.zsme.R
import pl.vemu.zsme.remembers.Prefs
import pl.vemu.zsme.remembers.rememberStringPreference
import pl.vemu.zsme.ui.components.transitions
import pl.vemu.zsme.ui.post.PostWorker
import pl.vemu.zsme.ui.theme.MainTheme
import soup.compose.material.motion.animation.rememberSlideDistance
import java.util.concurrent.TimeUnit

val Context.dataStore by preferencesDataStore(name = "settings")
var fullScreen by mutableStateOf(false)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Without this Android 34+ displays wrong theme, when launched with Android Studio.
        if (BuildConfig.DEBUG) setTheme(R.style.MainTheme)

        enableEdgeToEdge()

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
        createNotificationChannel()
        setupWorker()
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

        Scaffold(bottomBar = {
            BottomBar(navController)
        }) { innerPadding ->
            DestinationsNavHost(
                navGraph = NavGraphs.root,
                navController = navController,
                engine = navEngine,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            )
        }
    }

    @Composable
    private fun BottomBar(
        navController: NavController,
    ) {
        val currentDestination by navController.currentScreenAsState()

        AnimatedVisibility(
            !fullScreen,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            NavigationBar {
                BottomNavItem.values().forEach { item ->
                    val selected = currentDestination == item.destination

                    NavigationBarItem(
                        label = { Text(stringResource(item.label)) },
                        selected = selected,
                        icon = {
                            Icon(
                                imageVector = if (selected) item.iconFilled else item.icon,
                                contentDescription = stringResource(item.label),
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                        onClick = onClick@{
                            if (selected) {
                                navController.popBackStack(
                                    item.destination.startRoute,
                                    false,
                                )
                                return@onClick
                            }

                            navController.navigate(item.destination) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        })
                }
            }
        }
    }

    @Preview
    @Composable
    private fun BottomBarPreview() {
        val navController = rememberNavController()
        BottomBar(
            navController = navController
        )
    }

    @Suppress("RedundantSuspendModifier")
    private suspend fun setupRemoteConfig() {
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

    private fun setupWorker() {
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val worker = PeriodicWorkRequestBuilder<PostWorker>(
            3L, TimeUnit.HOURS, 15L, TimeUnit.MINUTES
        ).setConstraints(constraints).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SyncPostWorker", ExistingPeriodicWorkPolicy.KEEP, worker
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            getString(R.string.app_name),
            getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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
* timetable widget
* zsme.png drawable to svg
* onProvideAssistContent
* deeplinks
* formatter kotlin
* animations
* text overload with resid
* replace not null with let
* webview table full width
* */
