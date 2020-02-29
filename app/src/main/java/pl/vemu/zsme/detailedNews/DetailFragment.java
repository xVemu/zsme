package pl.vemu.zsme.detailedNews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pl.vemu.zsme.databinding.FragmentDetailNewsBinding;
import pl.vemu.zsme.newsFragment.NewsItem;

public class DetailFragment extends Fragment implements IAsyncTaskContext {

    private FragmentDetailNewsBinding binding;

    public DetailFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailNewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // TODO parse photos
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NewsItem newsItem = DetailFragmentArgs.fromBundle(getArguments()).getNewsItem();
        binding.title.setText(newsItem.getTitle());
        binding.webView.setWebViewClient(new DetailWebClient());
        new DownloadDetailedNews(this).execute(newsItem.getUrl());
    }

    @Override
    public void setDetailText(String text) {
        binding.webView.loadData(text, "text/html", "UTF-8");
    }

    @Override
    public void setProgress(int progress) {
        binding.progressBar.setProgress(progress);
    }

    @Override
    public void setProgressVisibility(int progressVisibility) {
        binding.progressBar.setVisibility(progressVisibility);
    }

}
