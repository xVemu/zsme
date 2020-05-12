package pl.vemu.zsme.detailedNews;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import java.util.concurrent.ExecutionException;

import pl.vemu.zsme.databinding.FragmentDetailNewsBinding;
import pl.vemu.zsme.newsFragment.NewsItem;

public class DetailFragment extends Fragment implements IAsyncTaskContext, Html.ImageGetter {

    private FragmentDetailNewsBinding binding;

    public DetailFragment() {
    }

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NewsItem newsItem = DetailFragmentArgs.fromBundle(getArguments()).getNewsItem();
        binding.title.setText(newsItem.getTitle());
//        binding.text.setMovementMethod(new ScrollingMovementMethod());
        binding.text.setMovementMethod(LinkMovementMethod.getInstance());
        new DownloadDetailedNews(this).execute(newsItem.getUrl());
    }

    @Override
    public void setDetailText(String text) {
        binding.text.setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT, this, null));
    }

    @Override
    public void setProgress(int progress) {
        binding.progressBar.setProgress(progress);
    }

    @Override
    public void setProgressVisibility(int progressVisibility) {
        binding.progressBar.setVisibility(progressVisibility);
    }

    @Override
    public Drawable getDrawable(String source) {
        try {
            return new LoadImages(this).execute(source).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
