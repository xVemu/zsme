package pl.vemu.zsme.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import androidx.work.*
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.datastore.manager.PreferenceRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import pl.vemu.zsme.R
import pl.vemu.zsme.surfaceColorWithElevation
import pl.vemu.zsme.ui.more.Contact
import pl.vemu.zsme.ui.more.More
import pl.vemu.zsme.ui.more.Settings
import pl.vemu.zsme.ui.post.Post
import pl.vemu.zsme.ui.post.PostWorker
import pl.vemu.zsme.ui.post.detail.Gallery
import pl.vemu.zsme.ui.post.detail.detail
import pl.vemu.zsme.ui.theme.MainTheme
import pl.vemu.zsme.ui.timetable.Lesson
import pl.vemu.zsme.ui.timetable.Timetable
import java.util.concurrent.TimeUnit

val Context.dataStore by preferencesDataStore(name = "settings")

/*TODO change theme in xml*/

@ExperimentalPagingApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val themePreference = PreferenceRequest(
        key = stringPreferencesKey("theme"),
        defaultValue = "system"
    )
    private val languagePreference = PreferenceRequest(
        key = stringPreferencesKey("language"),
        defaultValue = "system"
    )

    @ExperimentalFoundationApi
    @ExperimentalCoilApi
    @ExperimentalCoroutinesApi
    @ExperimentalComposeUiApi
    @ExperimentalMaterialApi
    @ExperimentalMaterial3Api
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStoreManager = DataStoreManager(this.dataStore)
        setContent {
            val theme by dataStoreManager.getPreferenceFlow(themePreference)
                .collectAsState(initial = "system")
            MainTheme(if (theme == "system") isSystemInDarkTheme() else theme.toBoolean()) {
                Main()
            }
            LaunchedEffect("changeLanguage") {
                dataStoreManager.getPreferenceFlow(languagePreference).collectLatest { lang ->
                    Lingver.getInstance().apply {
                        if (lang == "system") setFollowSystemLocale(this@MainActivity)
                        else setLocale(this@MainActivity, lang)
                    }
                }
            }
        }
        setupNotification()
    }


    @ExperimentalFoundationApi
    @ExperimentalCoroutinesApi
    @ExperimentalCoilApi
    @ExperimentalComposeUiApi
    @ExperimentalMaterialApi
    @ExperimentalMaterial3Api
    @ExperimentalPagerApi
    @Preview
    @Composable
    private fun Main() {
        val navController = rememberNavController()
        val backStack by navController.currentBackStackEntryAsState()
        val currentDestination = backStack?.destination
        var action = @Composable {}
        changeSystemBars()
        Scaffold(
            topBar = {
                TopBar(navController, currentDestination, action)
            },
            bottomBar = {
                BottomBar(currentDestination, navController)
            }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.POST.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                navigation("post", BottomNavItem.POST.route) {
                    composable("post") { Post(navController) }
                    composable(
                        route = "detail/{postModelId}",
                        arguments = listOf(
                            navArgument(
                                "postModelId"
                            ) {
                                type = NavType.IntType /*TODO change to parcelable*/
                            }
                        )
                    ) { backStack ->
                        backStack.arguments?.getInt("postModelId")
                            ?.let { postModelId ->
                                action = detail(navController, postModelId)
                            }
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
                }

                navigation("timetable", BottomNavItem.TIMETABLE.route) {
                    composable("timetable") { Timetable(navController) }
                    composable(
                        route = "lesson/{url}",
                        arguments = listOf(
                            navArgument("url") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStack ->
                        backStack.arguments?.getString("url")?.let { url ->
                            Lesson(url)
                        }
                    }
                }

                navigation("more", BottomNavItem.MORE.route) {
                    composable("more") { More(navController) }
                    composable("contact") { Contact() }
                    composable("settings") { Settings() }
                }
            }
        }
    }

    @Composable
    private fun changeSystemBars() {
        val systemUiController = rememberSystemUiController()
        systemUiController.setNavigationBarColor(
            MaterialTheme.colorScheme.surfaceColorWithElevation(
                3.dp
            )
        )
        systemUiController.setStatusBarColor(MaterialTheme.colorScheme.surface)
    }

    @Composable
    private fun BottomBar(
        currentDestination: NavDestination?,
        navController: NavHostController
    ) {
        NavigationBar(
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            BottomNavItem.values().forEach { item ->
                NavigationBarItem(
                    label = { Text(text = stringResource(id = item.title)) },
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(id = item.title)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
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
    private fun TopBar(
        navController: NavController,
        currentDestination: NavDestination?,
        action: @Composable () -> Unit
    ) {
        val backArrow = @Composable {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.back_button),
                )
            }
        }
        val startDestination = currentDestination?.parent?.startDestinationRoute
        val text = @Composable {
            Text(
                text = stringResource(
                    id = resources.getIdentifier(
                        startDestination ?: "app_name",
                        "string",
                        packageName
                    )
                )
            )
        }
        if (startDestination != currentDestination?.route) //TODO scroll behaviour
            SmallTopAppBar(
                navigationIcon = backArrow,
                title = text,
                actions = { action() }
            )
        else CenterAlignedTopAppBar(
            title = text,
            actions = { action() }
        )
    }

    enum class BottomNavItem(val route: String, val icon: ImageVector, @StringRes val title: Int) {

        POST("postNav", Icons.Rounded.DynamicFeed, R.string.post), //TODO web icon?
        TIMETABLE("timetableNav", Icons.Rounded.EventNote, R.string.timetable),
        MORE("moreNav", Icons.Rounded.Subject, R.string.more);

    }

    private fun setupNotification() { //TODO
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val worker = PeriodicWorkRequestBuilder<PostWorker>(
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
* fontsize to fontstyle
* deep links
* lingver change language when aab instead of apk
* internet refresh
* shortcuts
* notification
* detail from link
* */
}