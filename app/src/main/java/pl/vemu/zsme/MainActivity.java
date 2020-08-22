package pl.vemu.zsme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.appbar.AppBarLayout;

import java.util.concurrent.TimeUnit;

import pl.vemu.zsme.databinding.ActivityMainBinding;
import pl.vemu.zsme.newsFragment.NewsFragmentDirections;
import pl.vemu.zsme.newsFragment.NewsWorker;
import pl.vemu.zsme.timetableFragment.Login;
import pl.vemu.zsme.timetableFragment.timetable.TimetableFragmentDirections;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {

    private NavController navController;
    private ActivityMainBinding binding;

    //TODO rewrite
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setSupportActionBar(binding.toolbar);
        setContentView(binding.getRoot());
        setupNavigationBar();
        createNotificationChannel();
        setupNotification();
        setupIntent();
    }


    private void setupIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String url = intent.getStringExtra("url");
        if (url != null) {
            navController.navigate(NewsFragmentDirections.actionNewsToDetailFragment(url));
        } else if ("pl.vemu.zsme.shortcut.TIMETABLE".equals(action))
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
        navController.addOnDestinationChangedListener(this);
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
        NavigationUI.setupWithNavController(binding.toolbar, navController, configuration);
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) binding.toolbar.getLayoutParams();
        if (destination.getId() == R.id.newsFragment)
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        else params.setScrollFlags(0);
        if (!Login.INSTANCE.isLogged() && destination.getId() == R.id.timetableFragment)
            controller.navigate(TimetableFragmentDirections.actionTimetableFragmentToLoginFragment());
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel("ZSME", name, importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
