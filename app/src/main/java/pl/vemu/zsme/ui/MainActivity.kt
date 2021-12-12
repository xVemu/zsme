package pl.vemu.zsme.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.assist.AssistContent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.work.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.*
import com.google.android.play.core.review.ReviewManagerFactory
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.datastore.manager.PreferenceRequest
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
class MainActivity : ComponentActivity() {

    private val themePreference = PreferenceRequest(
        key = stringPreferencesKey("theme"),
        defaultValue = "system"
    )
    private val languagePreference = PreferenceRequest(
        key = stringPreferencesKey("language"),
        defaultValue = "system"
    )

    private var postLink: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    else setLocale(this@MainActivity, lang)
                }
            }
        }
        createNotificationChannel()
        setupNotification()
        lifecycleScope.launchWhenCreated {
            try {
                updateApp()
                reviewApp()
            } catch (e: Exception) {
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun Main() {
        val navController = rememberAnimatedNavController()
        val backStack by navController.currentBackStackEntryAsState()
        val currentDestination = backStack?.destination
        val scrollBehavior = remember { TopAppBarDefaults.enterAlwaysScrollBehavior() }
        ChangeSystemBars()
        Scaffold(
            topBar = {
                TopBar(navController, currentDestination, scrollBehavior)
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
                        exitTransition = Transitions.exitTransition,
                        popEnterTransition = Transitions.popEnterTransition
                    ) {
                        postLink = null
                        Post(navController, scrollBehavior)
                    }
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
                        exitTransition = Transitions.exitTransition,
                        popEnterTransition = Transitions.popEnterTransition
                    ) { backStack ->
                        backStack.arguments?.getString("slug")?.let { slug ->
                            postLink = detail(navController, 0, slug = slug) //TODO slug handle
                        } ?: backStack.arguments?.getInt("postModelId")
                            ?.let { postModelId ->
                                postLink = detail(navController, postModelId = postModelId)
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
                        exitTransition = Transitions.fadeOut,
                        popEnterTransition = Transitions.fadeIn
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
                        exitTransition = Transitions.exitTransition,
                        popEnterTransition = Transitions.popEnterTransition
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
                    label = { Text(text = stringResource(item.title)) },
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(item.title)
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
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        val backArrow = @Composable {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                )
            }
        }
        val startDestination = currentDestination?.parent?.startDestinationRoute
        val text = @Composable {
            Text(
                text = stringResource(
                    resources.getIdentifier(
                        startDestination ?: "app_name",
                        "string",
                        packageName
                    )
                )
            )
        }
        if (startDestination != currentDestination?.route)
            SmallTopAppBar(
                navigationIcon = backArrow,
                title = text,
                actions = {
                    if (currentDestination?.route?.startsWith("detail") == true) {
                        ShareButton()
                    }
                }
            )
        else CenterAlignedTopAppBar(
            title = text,
            scrollBehavior = if (currentDestination?.route == BottomNavItem.POST.startDestination) scrollBehavior else null
        )
    }

    @Composable
    private fun ShareButton() {
        val context = LocalContext.current
        IconButton(onClick = {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, postLink)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, null)
            context.startActivity(shareIntent)
        }) {
            Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = stringResource(R.string.share)
            )
        }
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

    private suspend fun reviewApp() { // TODO days delay https://developer.android.com/guide/playcore/in-app-review/test
        val manager = ReviewManagerFactory.create(applicationContext)
        val request = manager.requestReview()
        manager.launchReview(this, request)
    }

    private suspend fun updateApp() { // TODO https://developer.android.com/guide/playcore/in-app-updates/test
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

    override fun onProvideAssistContent(outContent: AssistContent?) {
        super.onProvideAssistContent(outContent)
        postLink?.let {
            outContent?.webUri = Uri.parse(it)
        }
    }

/* TODO check
* previews
* internet refresh
* */
}