package pl.vemu.zsme.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.work.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.datastore.manager.PreferenceRequest
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
        createNotificationChannel()
        setupNotification()
    }

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun Main() {
        val navController = rememberAnimatedNavController()
        val backStack by navController.currentBackStackEntryAsState()
        val currentDestination = backStack?.destination
        var action = @Composable {} // TODO not changing to share button on detail
        val scrollBehavior = remember { TopAppBarDefaults.enterAlwaysScrollBehavior() }
        ChangeSystemBars()
        Scaffold(
            topBar = {
                TopBar(navController, currentDestination, scrollBehavior, action)
            },
            bottomBar = {
                BottomBar(currentDestination, navController)
            }) { innerPadding ->
            AnimatedNavHost(
                navController = navController,
                startDestination = BottomNavItem.POST.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                navigation(BottomNavItem.POST.startDestination, BottomNavItem.POST.route) {
                    composable(
                        route = BottomNavItem.POST.startDestination,
                        deepLinks = listOf(navDeepLink { uriPattern = "https://zsme.tarnow.pl/" }),
                        exitTransition = Transitions.exitTransitionStartDestination,
                        popEnterTransition = Transitions.popEnterTransitionStartDestination
                    ) { Post(navController, scrollBehavior) }
                    composable(
                        route = "detail/{postModelId}?slug={slug}",
                        arguments = listOf(
                            navArgument("postModelId") {
                                defaultValue = 0
                                type = NavType.IntType /*TODO change to parcelable*/
                            },
                            navArgument("slug") {
                                nullable = true
                                type = NavType.StringType
                            }
                        ),
                        deepLinks = listOf(
                            navDeepLink { uriPattern = "https://zsme.tarnow.pl/wp/{slug}/" },
                            navDeepLink { uriPattern = "https://zsme.tarnow.pl/wp/{slug}" },
                            navDeepLink { uriPattern = "zsme://detail/{postModelId}" }
                        ),
                        enterTransition = Transitions.enterTransition,
                        popExitTransition = Transitions.popExitTransition,
                        exitTransition = Transitions.exitTransitionStartDestination,
                        popEnterTransition = Transitions.popEnterTransitionStartDestination
                    ) { backStack ->
                        backStack.arguments?.getString("slug")?.let { slug ->
                            action = detail(navController, 0, slug = slug) //TODO slug handle
                        } ?: backStack.arguments?.getInt("postModelId")
                            ?.let { postModelId ->
                                action = detail(navController, postModelId = postModelId)
                            }
                    }
                    composable(
                        route = "gallery?images={images}",
                        arguments = listOf(
                            navArgument("images") {
                                nullable = true
                                type = NavType.StringType
                            }
                        ),
                        enterTransition = Transitions.enterTransition,
                        popExitTransition = Transitions.popExitTransition
                    ) { backStack ->
                        backStack.arguments?.getString("images")?.let { Gallery(it) }
                    } /*TODO argmuments string array*/
                }

                navigation(
                    BottomNavItem.TIMETABLE.startDestination,
                    BottomNavItem.TIMETABLE.route
                ) {
                    composable(
                        route = BottomNavItem.TIMETABLE.startDestination,
                        deepLinks = listOf(
                            navDeepLink { uriPattern = "zsme://timetable" },
                            navDeepLink { uriPattern = "https://zsme.tarnow.pl/plan/" }
                        ),
                        exitTransition = Transitions.fadeOutStartDestination,
                        popEnterTransition = Transitions.fadeInStartDestination
                    ) { Timetable(navController) }
                    composable(
                        route = "lesson/{url}",
                        arguments = listOf(
                            navArgument("url") {
                                type = NavType.StringType
                            }
                        ),
                        enterTransition = { _, _ ->
                            expandIn(expandFrom = Alignment.Center) + fadeIn()
                        },
                        popExitTransition = { _, _ ->
                            shrinkOut(shrinkTowards = Alignment.Center) + fadeOut()
                        }
                    ) { backStack ->
                        backStack.arguments?.getString("url")?.let { url ->
                            Lesson(url)
                        }
                    }
                }

                navigation(BottomNavItem.MORE.startDestination, BottomNavItem.MORE.route) {
                    composable(
                        route = BottomNavItem.MORE.startDestination,
                        deepLinks = listOf(navDeepLink { uriPattern = "zsme://more" }),
                        exitTransition = Transitions.exitTransitionStartDestination,
                        popEnterTransition = Transitions.popEnterTransitionStartDestination
                    ) {
                        More(navController)
                    }
                    composable(
                        route = "contact",
                        deepLinks = listOf(navDeepLink { //TODO backstack
                            uriPattern = "https://zsme.tarnow.pl/wp/kontakt/"
                        }),
                        enterTransition = Transitions.enterTransition,
                        popExitTransition = Transitions.popExitTransition
                    ) { Contact() }
                    composable(
                        route = "settings",
                        enterTransition = Transitions.enterTransition,
                        popExitTransition = Transitions.popExitTransition
                    ) { Settings() }
                }
            }
        }
    }

    @Composable
    private fun ChangeSystemBars() {
        val systemUiController = rememberSystemUiController()
        systemUiController.setNavigationBarColor(
            MaterialTheme.colorScheme.surfaceColorWithElevation(3.dp)
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar(
        navController: NavController,
        currentDestination: NavDestination?,
        scrollBehavior: TopAppBarScrollBehavior,
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
            actions = { action() },
            scrollBehavior = if (currentDestination?.route == "post") {
                scrollBehavior
            } else null
        )
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

/* TODO check
* previews
* deep links
* internet refresh
* */
}