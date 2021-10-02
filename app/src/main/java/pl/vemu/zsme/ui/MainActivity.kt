package pl.vemu.zsme.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DynamicFeed
import androidx.compose.material.icons.rounded.EventNote
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Subject
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.ExperimentalPagingApi
import androidx.work.*
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.datastore.manager.PreferenceRequest
import pl.vemu.zsme.R
import pl.vemu.zsme.newsFragment.NewsWorker
import pl.vemu.zsme.ui.more.Contact
import pl.vemu.zsme.ui.more.More
import pl.vemu.zsme.ui.more.Settings
import pl.vemu.zsme.ui.post.Post
import pl.vemu.zsme.ui.post.detail.Detail
import pl.vemu.zsme.ui.post.detail.Gallery
import pl.vemu.zsme.ui.timetable.TimetableFragment
import java.util.concurrent.TimeUnit

val Context.dataStore by preferencesDataStore(name = "settings")

/*TODO change theme in xml*/

@ExperimentalPagingApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity()/*, OnDestinationChangedListener*/ {

    private val theme = PreferenceRequest(
            key = stringPreferencesKey("theme"),
            defaultValue = "-1"
    )
    private val language = PreferenceRequest(
            key = stringPreferencesKey("language"),
            defaultValue = "system"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStoreManager = DataStoreManager(this.dataStore)
        setContent {
            val themeFlow by dataStoreManager.getPreferenceFlow(theme)
                    .collectAsState(initial = "system")
            MainTheme(if (themeFlow == "system") isSystemInDarkTheme() else themeFlow.toBoolean()) {
                Main()
            }
        }
        /*lifecycleScope.launch {
            dataStoreManager.getPreferenceFlow(language).collect { lang ->
                Lingver.getInstance().apply {
                    if (lang == "system") setFollowSystemLocale(this@MainActivity)
                    else setLocale(this@MainActivity, lang)
                }
                this@MainActivity.recreate()
            }
        }TODO*/
        //        createNotificationChannel()
        //        setupNotification()
    }


    @Preview
    @Composable
    private fun Main() {
        val navController = rememberNavController()
        val backStack by navController.currentBackStackEntryAsState()
        val currentDestination = backStack?.destination
        Scaffold(
                topBar = {
                    TopBar(currentDestination)
                },
                bottomBar = {
                    BottomBar(currentDestination, navController)
                }) { innerPadding ->
            NavHost(
                    navController = navController,
                    startDestination = "post",
                    modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.POST.route) { Post(navController) }
                composable(
                        route = "detail/{postModelId}",
                        arguments = listOf(
                                navArgument(
                                        "postModelId"
                                ) {
                                    type = NavType.IntType /*TODO change to parcerable*/
                                }
                        )
                ) { backStack ->
                    backStack.arguments?.getInt("postModelId")
                            ?.let { postModelId -> Detail(navController, postModelId) }
                }
                composable(
                        route = "gallery?images={images}",
                        arguments = listOf(
                                navArgument("images") {
                                    nullable = true
                                    type = NavType.StringType
                                }
                        )
                ) { backStack ->
                    backStack.arguments?.getString("images")?.let { Gallery(it) }
                } /*TODO argmuments string array*/

                composable(BottomNavItem.TIMETABLE.route) { TimetableFragment() }

                //                navigation(startDestination = BottomNavItem.MORE.route)
                composable(BottomNavItem.MORE.route) { More(navController) }
                composable("contact") { Contact() }
                composable("settings") { Settings() }
            }
        }
    }

    @Composable
    private fun BottomBar(
            currentDestination: NavDestination?,
            navController: NavHostController
    ) {
        BottomNavigation {
            BottomNavItem.values().forEach { item ->
                BottomNavigationItem(
                        icon = {
                            Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(id = item.title)
                            )
                        },
                        label = { Text(text = stringResource(id = item.title)) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true, //TODO backstack
                        selectedContentColor = MaterialTheme.colors.secondary,
                        unselectedContentColor = Color.Gray,
                        onClick = {
                            navController.navigate(item.route) {
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

    /*TODO hide when scrolling*/
    @Composable
    private fun TopBar(currentDestination: NavDestination?) {
        TopAppBar(
                title = {
                    Text(
                            text = "ZSME"
                            /*TODO stringResource(
                                id = resources.getIdentifier(
                                    currentDestination?.route ?: "app_name",
                                    "string",
                                    packageName
                                )
                            )*/,
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(20.dp, 4.dp, 4.dp, 4.dp)
                    )
                },
                actions =
                {
                    currentDestination?.route?.let {
                        when {
                            it == "post" -> {

                            }
                            it.startsWith("detail") -> {
                                IconButton(onClick = {

                                }) { //TODO
                                    Icon(
                                            imageVector = Icons.Rounded.Share,
                                            contentDescription = stringResource(R.string.share)
                                    )
                                }
                            }
                        }
                    }
                }
        )
    }

    enum class BottomNavItem(val route: String, val icon: ImageVector, @StringRes val title: Int) {

        POST("post", Icons.Rounded.DynamicFeed, R.string.post), //TODO web icon?
        TIMETABLE("timetable", Icons.Rounded.EventNote, R.string.timetable),
        MORE("more", Icons.Rounded.Subject, R.string.more);

    }

    private fun setupNotification() { //TODO
        val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val worker = PeriodicWorkRequestBuilder<NewsWorker>(
                30L,
                TimeUnit.MINUTES,
                15L,
                TimeUnit.MINUTES
        ).setConstraints(constraints).build()
        WorkManager.getInstance(applicationContext)
                .enqueueUniquePeriodicWork("SyncPostWorker", ExistingPeriodicWorkPolicy.KEEP, worker)
    }

    /*override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val params = binding.toolbar.layoutParams as AppBarLayout.LayoutParams
        if (destination.id == R.id.postFragment) params.scrollFlags =
            (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
        else params.scrollFlags = 0
    }*/

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ZSME", name, importance)
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    /* TODO check
    * internet refresh
    * shortcuts
    * notification
    * detail from link
    * */
}