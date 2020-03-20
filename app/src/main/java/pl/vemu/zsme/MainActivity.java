package pl.vemu.zsme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import pl.vemu.zsme.databinding.ActivityMainBinding;
import pl.vemu.zsme.newsFragment.NewsFragmentDirections;
import pl.vemu.zsme.newsFragment.NewsItem;
import pl.vemu.zsme.newsFragment.NewsWorker;
import pl.vemu.zsme.timetableFragment.timetable.TimetableFragmentDirections;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        int theme = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("theme", "-1"));
        AppCompatDelegate.setDefaultNightMode(theme);
        setContentView(binding.getRoot());
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(binding.bottomNav.getMenu()).build();
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (!STATIC.LOGGED_IN && destination.getId() == R.id.timetableFragment)
                navController.navigate(TimetableFragmentDirections.actionTimetableFragmentToLoginFragment());
        });
        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Toast.makeText(this, "Wystąpił błąd!", Toast.LENGTH_LONG).show());
        createNotificationChannel();
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest worker = new PeriodicWorkRequest.Builder(NewsWorker.class, 30L, TimeUnit.MINUTES, 15L, TimeUnit.MINUTES)
                .setConstraints(constraints).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("SyncNewsWorker", ExistingPeriodicWorkPolicy.KEEP, worker);
        if (getIntent() != null && getIntent().getSerializableExtra("NewsItem") != null)
            navController.navigate(NewsFragmentDirections.actionNewsToDetailFragment((NewsItem) getIntent().getSerializableExtra("NewsItem")));
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel("ZSME", name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
