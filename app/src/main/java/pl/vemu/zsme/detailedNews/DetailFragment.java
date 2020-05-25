package pl.vemu.zsme.detailedNews;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.navigation.Navigation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.FragmentDetailNewsBinding;

public class DetailFragment extends Fragment implements IAsyncTaskContext, View.OnClickListener {

    private FragmentDetailNewsBinding binding;
    private String url;
    private ArrayList<String> images = new ArrayList<>();

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
        url = DetailFragmentArgs.fromBundle(getArguments()).getUrl();
        setHasOptionsMenu(true);
        binding.text.setMovementMethod(LinkMovementMethod.getInstance());
        if (!url.startsWith("http")) url = getString(R.string.zsme_default_link) + url;
        new DownloadDetailedNews(this).execute(url);
        binding.gallery.setOnClickListener(this);
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
            intent.putExtra(Intent.EXTRA_TEXT, url);
            intent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(intent, null);
            startActivity(shareIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setDetailText(String text) {
        Document parse = Jsoup.parse(text);
        Elements photos = parse.select("img");
        if (photos.size() != 0) {
            binding.gallery.setVisibility(View.VISIBLE);
            for (Element photo : photos) {
                images.add(photo.attr("src").replace("thumbs/thumbs_", ""));
                photo.remove();
            }
        }
        binding.text.setText(HtmlCompat.fromHtml(parse.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT, null, null));
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
    public void onClick(View v) {
        String[] imagesArray = new String[this.images.size()];
        this.images.toArray(imagesArray);
        Navigation.findNavController(v).navigate(DetailFragmentDirections.actionDetailFragmentToGalleryFragment(imagesArray));
    }
}
