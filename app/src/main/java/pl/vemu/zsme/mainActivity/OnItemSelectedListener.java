package pl.vemu.zsme.mainActivity;

import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OnItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {

    private final Context ctx;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Toast.makeText(ctx, item.getTitle(), Toast.LENGTH_SHORT).show();
        return false;
    }
}
