package pl.vemu.zsme.detailedNews;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import java.util.concurrent.ExecutionException;

import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.FragmentDetailNewsBinding;
import pl.vemu.zsme.newsFragment.NewsItem;

public class DetailFragment extends Fragment implements IAsyncTaskContext, Html.ImageGetter {

    private FragmentDetailNewsBinding binding;
    private NewsItem newsItem;

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
        newsItem = DetailFragmentArgs.fromBundle(getArguments()).getNewsItem();
        binding.title.setText(newsItem.getTitle());
        setHasOptionsMenu(true);
        binding.text.setMovementMethod(LinkMovementMethod.getInstance());
        new DownloadDetailedNews(this).execute(newsItem.getUrl());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_share) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, newsItem.getUrl());
            intent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(intent, null);
            startActivity(shareIntent);
        }
        return super.onOptionsItemSelected(item);
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
