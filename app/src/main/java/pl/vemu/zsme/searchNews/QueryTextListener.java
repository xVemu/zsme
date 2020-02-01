package pl.vemu.zsme.searchNews;

import android.util.Log;
import android.widget.SearchView;

import lombok.RequiredArgsConstructor;
import pl.vemu.zsme.mainActivity.DownloadNews;
import pl.vemu.zsme.mainActivity.NewsAdapter;

@RequiredArgsConstructor
public class QueryTextListener implements SearchView.OnQueryTextListener {

    private final NewsAdapter adapter;

    @Override
    public boolean onQueryTextSubmit(String query) {
        new DownloadNews(1, query).execute(adapter);
        Log.d("xD", query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
