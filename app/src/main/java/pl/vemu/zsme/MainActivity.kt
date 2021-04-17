package pl.vemu.zsme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.google.android.material.appbar.AppBarLayout
import pl.vemu.zsme.databinding.ActivityMainBinding
import pl.vemu.zsme.newsFragment.NewsWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), OnDestinationChangedListener {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    // TODO hilt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupNavigationBar()
        createNotificationChannel()
        setupNotification()
    }

    private fun setupNotification() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val worker = PeriodicWorkRequestBuilder<NewsWorker>(30L, TimeUnit.MINUTES, 15L, TimeUnit.MINUTES).setConstraints(constraints).build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("SyncNewsWorker", ExistingPeriodicWorkPolicy.KEEP, worker)
    }

    private fun setupNavigationBar() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val configuration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, configuration)
        binding.bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        val params = binding.toolbar.layoutParams as AppBarLayout.LayoutParams
        if (destination.id == R.id.newsFragment) params.scrollFlags = (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
        else params.scrollFlags = 0
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ZSME", name, importance)
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    /* TODO check
    * internet refresh
    * shortcuts
    * notification
    * scrolling hide toolbar
    * detail from link
    *
    * */
}