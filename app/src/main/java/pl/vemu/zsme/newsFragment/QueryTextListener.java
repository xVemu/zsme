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
        NewsAdapter adapter = (NewsAdapter) recyclerView.getAdapter();
        adapter.removeAllItems();
        Queries queryObj;
        if (query.startsWith("author/")) queryObj = new Queries.Author(1, query);
        else queryObj = new Queries.Search(1, query);
        new DownloadNews(queryObj).execute(adapter);
        recyclerView.addOnScrollListener(new RecScrollListener(queryObj));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
