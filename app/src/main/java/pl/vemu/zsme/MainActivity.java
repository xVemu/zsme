package pl.vemu.zsme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private NewsAdapter adapter;
    private ArrayList<NewsItem> newsItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recView = findViewById(R.id.recView);
        recView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NewsAdapter(newsItems);
        new DownloadNews().execute(adapter);
        recView.setAdapter(adapter);
    }
}
