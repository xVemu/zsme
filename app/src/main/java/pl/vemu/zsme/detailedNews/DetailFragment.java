package pl.vemu.zsme.detailedNews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import pl.vemu.zsme.R;
import pl.vemu.zsme.newsFragment.NewsItem;

public class DetailFragment extends Fragment implements IAsyncTaskContext {

    private TextView detailText;
    private ProgressBar progressBar;
    private NewsItem newsItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_news, container, false);
        newsItem = DetailFragmentArgs.fromBundle(getArguments()).getNewsItem();
        TextView title = view.findViewById(R.id.detailTitle);
        TextView author = view.findViewById(R.id.detailAuthor);
        TextView date = view.findViewById(R.id.detailDate);
        title.setText(newsItem.getTitle());
        author.setText(newsItem.getAuthor());
        date.setText(newsItem.getDate());
        return view;
    }

    // TODO parse html
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        detailText = view.findViewById(R.id.detailText);
        progressBar = view.findViewById(R.id.progressBarDetail);
        detailText.setMovementMethod(new ScrollingMovementMethod());
        new DownloadDetailedNews(this).execute(newsItem.getUrl());
    }

    @Override
    public void setDetailText(String text) {
        detailText.setText(text);
    }

    @Override
    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    @Override
    public void setProgressVisibility(int progressVisibility) {
        progressBar.setVisibility(progressVisibility);
    }
}
