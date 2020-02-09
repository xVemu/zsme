package pl.vemu.zsme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pl.vemu.zsme.newsFragment.NewsFragmentDirections;
import pl.vemu.zsme.timetableFragment.TimetableFragment;
import pl.vemu.zsme.timetableFragment.TimetableFragmentDirections;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(navigationView.getMenu()).build();
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (!STATIC.LOGGED_IN && destination.getId() == R.id.timetableFragment) navController.navigate(TimetableFragmentDirections.actionTimetableFragmentToLoginFragment());
        });
        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }
}
