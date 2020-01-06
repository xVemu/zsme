package pl.vemu.zsme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<NewsItem> newsItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recView = findViewById(R.id.recView);
        recView.setLayoutManager(new LinearLayoutManager(this));

        NewsAdapter adapter = new NewsAdapter(newsItems);
        recView.setAdapter(adapter);
        recView.addOnScrollListener(new RecScrollListener());
        new DownloadNews(1).execute(adapter);
    }
}
