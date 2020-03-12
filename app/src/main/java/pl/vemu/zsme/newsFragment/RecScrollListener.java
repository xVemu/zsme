package pl.vemu.zsme.newsFragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecScrollListener extends RecyclerView.OnScrollListener {

    private int page = 2;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        NewsAdapter adapter = (NewsAdapter) recyclerView.getAdapter();
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if ( manager != null && adapter != null && manager.findLastCompletelyVisibleItemPosition() == adapter.getNewsItems().size() - 1) {
            new DownloadNews(page).execute(adapter);
            page++;
        }
    }
}
