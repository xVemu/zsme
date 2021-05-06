package pl.vemu.zsme.newsFragment;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import pl.vemu.zsme.ui.NewsFragmentVM;

public class NewsQueryTextListener implements SearchView.OnQueryTextListener {

    private final NewsFragmentVM viewmodel;
    private final RecyclerView recyclerView;

    public NewsQueryTextListener(NewsFragmentVM viewmodel, RecyclerView recyclerView) {
        this.viewmodel = viewmodel;
        this.recyclerView = recyclerView;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        recyclerView.clearOnScrollListeners();
//        viewmodel.clearList();
        Queries queryObj;
        if (query.startsWith("author/")) queryObj = new Queries.Author(query);
        else queryObj = new Queries.Search(query);
//        viewmodel.downloadNews(queryObj);
        recyclerView.addOnScrollListener(new NewsScrollListener(viewmodel, queryObj));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
