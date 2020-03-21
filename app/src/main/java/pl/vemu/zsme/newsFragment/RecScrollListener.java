package pl.vemu.zsme.newsFragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RecScrollListener extends RecyclerView.OnScrollListener {

    private int page = 2;
    private String query = null;

    public RecScrollListener(String query) {
        this.query = query;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        NewsAdapter adapter = (NewsAdapter) recyclerView.getAdapter();
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (manager != null && adapter != null && manager.findLastCompletelyVisibleItemPosition() == adapter.getNewsItems().size() - 1) {
            if (query != null) new DownloadNews(page, query).execute(adapter);
            else new DownloadNews(page).execute(adapter);
            page++;
        }
    }
}
