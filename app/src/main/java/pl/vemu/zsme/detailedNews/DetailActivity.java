package pl.vemu.zsme.detailedNews;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import pl.vemu.zsme.R;

public class DetailActivity extends AppCompatActivity implements IAsyncTaskContext {

    private TextView detailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detailText = findViewById(R.id.detailText);
        new DownloadDetailedNews(this).execute(getIntent().getStringExtra("url"));

    }

    @Override
    public void setParams(String text) {
        detailText.setText(text);
    }
}
