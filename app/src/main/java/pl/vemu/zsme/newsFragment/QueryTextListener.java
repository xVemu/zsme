package pl.vemu.zsme.newsFragment;

import androidx.appcompat.widget.SearchView;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class QueryTextListener implements SearchView.OnQueryTextListener {

    private final NewsAdapter adapter;

    @Override
    public boolean onQueryTextSubmit(String query) {
        new DownloadNews(1, query).execute(adapter);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
