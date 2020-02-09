package pl.vemu.zsme.detailedNews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pl.vemu.zsme.R;
import pl.vemu.zsme.newsFragment.NewsItem;

public class DetailFragment extends Fragment implements IAsyncTaskContext {

    private TextView detailText;
    private NewsItem newsItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_news, container, false);
    }

    // TODO parse html
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newsItem = DetailFragmentArgs.fromBundle(getArguments()).getNewsItem();
        detailText = view.findViewById(R.id.detailText);
        TextView title = view.findViewById(R.id.detailTitle);
        TextView author = view.findViewById(R.id.detailAuthor);
        TextView date = view.findViewById(R.id.detailDate);
        title.setText(newsItem.getTitle());
        author.setText(newsItem.getAuthor());
        date.setText(newsItem.getDate());
        new DownloadDetailedNews(this).execute(newsItem.getUrl());
    }

    @Override
    public void setDetailText(String text) {
        detailText.setText(text);
    }

}
