package pl.vemu.zsme.newsFragment;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NewsQueryTextListener implements SearchView.OnQueryTextListener {

    private final NewsFragmentVM viewmodel;
    private final RecyclerView recyclerView;

    @Override
    public boolean onQueryTextSubmit(String query) {
        recyclerView.clearOnScrollListeners();
        viewmodel.clearList();
        Queries queryObj;
        if (query.startsWith("author/")) queryObj = new Queries.Author(query);
        else queryObj = new Queries.Search(query);
        viewmodel.downloadNews(queryObj);
        recyclerView.addOnScrollListener(new NewsScrollListener(viewmodel, queryObj));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
