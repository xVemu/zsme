package pl.vemu.zsme.newsFragment;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueryTextListener implements SearchView.OnQueryTextListener {

    private final RecyclerView recyclerView;

    @Override
    public boolean onQueryTextSubmit(String query) {
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(new RecScrollListener(query));
        NewsAdapter adapter = (NewsAdapter) recyclerView.getAdapter();
        adapter.removeAllItems();
        new DownloadNews(1, query).execute(adapter);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
