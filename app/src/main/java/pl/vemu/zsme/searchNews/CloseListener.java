package pl.vemu.zsme.searchNews;

import android.widget.SearchView;

import lombok.RequiredArgsConstructor;
import pl.vemu.zsme.mainActivity.DownloadNews;
import pl.vemu.zsme.mainActivity.NewsAdapter;

@RequiredArgsConstructor
public class CloseListener implements SearchView.OnCloseListener {

    private final NewsAdapter adapter;

    @Override
    public boolean onClose() {
        new DownloadNews(1, "").execute(adapter);
        return false;
    }
}
