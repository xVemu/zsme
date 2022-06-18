package pl.vemu.zsme.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.*
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.navigate
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.datastore.manager.PreferenceRequest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pl.vemu.zsme.R
import pl.vemu.zsme.surfaceColorWithElevation
import pl.vemu.zsme.ui.post.PostWorker
import pl.vemu.zsme.ui.theme.MainTheme
import java.util.concurrent.TimeUnit

val Context.dataStore by preferencesDataStore(name = "settings")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themePreference = PreferenceRequest(
        key = stringPreferencesKey("theme"),
        defaultValue = "system"
    )
    private val languagePreference = PreferenceRequest(
        key = stringPreferencesKey("language"),
        defaultValue = "system"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val dataStoreManager = DataStoreManager(this.dataStore)
        setContent {
            val theme by dataStoreManager.getPreferenceFlow(themePreference)
                .collectAsState(initial = "system")
            MainTheme(if (theme == "system") isSystemInDarkTheme() else theme.toBoolean()) {
                Main()
            }
        }
        lifecycleScope.launch {
            dataStoreManager.getPreferenceFlow(languagePreference).collectLatest { lang ->
                Lingver.getInstance().apply {
                    if (lang == "system") setFollowSystemLocale(this@MainActivity)
                    else {
                        WebView(applicationContext).destroy() // fixes resetting language to system when it's first use of webview.
                        setLocale(this@MainActivity, lang)
                    }
                }
            }
        }
        createNotificationChannel()
        setupNotification()
        lifecycleScope.launchWhenCreated {
            @Suppress("EmptyCatchBlock", "TooGenericExceptionCaught", "SwallowedException")
            try {
                updateApp()
            } catch (e: Exception) {
            }
        }
    }

    @OptIn(
        ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class,
        ExperimentalMaterialNavigationApi::class
    )
    @Composable
    private fun Main() {
        val navController = rememberAnimatedNavController()
        ChangeSystemBars()
        Scaffold(
            bottomBar = {
                BottomBar(navController)
            }) { innerPadding ->
            DestinationsNavHost(
                navGraph = NavGraphs.root,
                navController = navController,
                engine = rememberAnimatedNavHostEngine(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }

    @Composable
    private fun BottomBar(
        navController: NavController
    ) {
        val currentDestination by navController.currentScreenAsState()
        NavigationBar {
            BottomNavItem.values().forEach { item ->
                NavigationBarItem(
                    label = { Text(stringResource(item.label)) },
                    selected = currentDestination == item.destination,
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(item.label)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    onClick = {
                        navController.navigate(item.destination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
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

    @Composable
    private fun ChangeSystemBars() {
        val systemUiController = rememberSystemUiController()
        systemUiController.setNavigationBarColor(
            MaterialTheme.colorScheme.surfaceColorWithElevation(3.dp)
        )
        systemUiController.setStatusBarColor(MaterialTheme.colorScheme.surface)
    }

    private fun setupNotification() {
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val worker = PeriodicWorkRequestBuilder<PostWorker>(
            3L,
            TimeUnit.HOURS,
            15L,
            TimeUnit.MINUTES
        ).setConstraints(constraints).build()
        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "SyncPostWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                worker
            )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.app_name),
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /*private suspend fun reviewApp() { // TODO days delay
        val manager = ReviewManagerFactory.create(applicationContext)
        val request = manager.requestReview()
        manager.launchReview(this, request)
    }*/

    private suspend fun updateApp() {
        val manager = AppUpdateManagerFactory.create(applicationContext)
        val appUpdateInfo = manager.requestAppUpdateInfo()
        if (!(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isFlexibleUpdateAllowed
                    && (appUpdateInfo.clientVersionStalenessDays ?: -1) >= 7
                    )
        ) return
        manager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.FLEXIBLE,
            this,
            0
        )
        manager.requestUpdateFlow().collect { appUpdateResult ->
            if (appUpdateResult is AppUpdateResult.Downloaded) appUpdateResult.completeUpdate()
        }
    }
}

/*TODO
* Scrollowanie przesuwa się w pewnym momencie
* timetable widget
* zsme.png drawable to svg
* onProvideAssistContent
* */