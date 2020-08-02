package pl.vemu.zsme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import pl.vemu.zsme.databinding.ActivityMainBinding;
import pl.vemu.zsme.newsFragment.NewsFragmentDirections;
import pl.vemu.zsme.newsFragment.NewsWorker;
import pl.vemu.zsme.timetableFragment.timetable.TimetableFragmentDirections;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupNavigationBar();
        createNotificationChannel();
        setupNotification();
        setupIntent();
    }


    private void setupIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String url = (String) intent.getSerializableExtra("url");
        if (url != null)
            navController.navigate(NewsFragmentDirections.actionNewsToDetailFragment(url));
        else if ("pl.vemu.zsme.shortcut.TIMETABLE".equals(action))
            navController.navigate(R.id.timetableFragment);
        else if ("pl.vemu.zsme.shortcut.MORE".equals(action))
            navController.navigate(R.id.moreFragment);
    }

    private void setupNotification() {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest worker = new PeriodicWorkRequest.Builder(NewsWorker.class, 30L, TimeUnit.MINUTES, 15L, TimeUnit.MINUTES)
                .setConstraints(constraints).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("SyncNewsWorker", ExistingPeriodicWorkPolicy.KEEP, worker);
    }

    private void setupNavigationBar() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(binding.bottomNav.getMenu()).build();
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (!STATIC.LOGGED_IN && destination.getId() == R.id.timetableFragment)
                navController.navigate(TimetableFragmentDirections.actionTimetableFragmentToLoginFragment());
        });
        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel("ZSME") == null) {
            CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel("ZSME", name, importance);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
