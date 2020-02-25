package pl.vemu.zsme.newsFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import pl.vemu.zsme.R;

public class NewsFragment extends Fragment implements IAsyncTaskContext {

    private NewsAdapter adapter;
    private TextView textView;
    private SwipeRefreshLayout refreshLayout;

    public NewsFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = view.findViewById(R.id.refreshNews);
        RecyclerView recView = view.findViewById(R.id.recViewNews);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);

        textView = view.findViewById(R.id.not_found);

        DownloadNews.setContext(this);

        setHasOptionsMenu(true);
        adapter = new NewsAdapter();
        recView.setAdapter(adapter);
        recView.addOnScrollListener(new RecScrollListener());
        new DownloadNews(1).execute(adapter);

        refreshLayout.setOnRefreshListener(() -> new DownloadNews(1).execute(adapter));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(new QueryTextListener(adapter));
        searchView.setOnCloseListener(() -> {
            searchView.onActionViewCollapsed();
            new DownloadNews(1, "").execute(adapter);
            return true;
        });
    }

    @Override
    public void setIsFound(boolean isFound) {
        refreshLayout.setRefreshing(false);
        if (isFound) {
            textView.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void startRefreshing() {
        refreshLayout.setRefreshing(true);
    }
}
