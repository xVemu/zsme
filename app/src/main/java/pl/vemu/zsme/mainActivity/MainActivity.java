package pl.vemu.zsme.mainActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pl.vemu.zsme.R;
import pl.vemu.zsme.searchNews.CloseListener;
import pl.vemu.zsme.searchNews.QueryTextListener;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recView = findViewById(R.id.recView);
        recView.setLayoutManager(new LinearLayoutManager(this));

        // sets adapter to download news
        NewsAdapter adapter = new NewsAdapter(this);
        recView.setAdapter(adapter);
        recView.addOnScrollListener(new RecScrollListener());
        new DownloadNews(1).execute(adapter);

        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);
        navigationView.setOnNavigationItemSelectedListener(new OnItemSelectedListener(this));

    }

    // Add search button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        NewsAdapter adapter = (NewsAdapter) recView.getAdapter();
        searchView.setOnQueryTextListener(new QueryTextListener(adapter));
        searchView.setOnCloseListener(new CloseListener(adapter));
        return true;
    }
}
