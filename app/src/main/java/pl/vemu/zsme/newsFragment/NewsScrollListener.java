package pl.vemu.zsme.newsFragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NewsScrollListener extends RecyclerView.OnScrollListener {

    private final NewsFragmentVM viewmodel;
    private final Queries query;


    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (manager != null && viewmodel.getList().getValue() != null && viewmodel.getList().getValue().size() != 0 && manager.findLastCompletelyVisibleItemPosition() == viewmodel.getList().getValue().size() - 1) {
            viewmodel.downloadNews(query);
        }
    }
}
