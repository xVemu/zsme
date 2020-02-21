package pl.vemu.zsme.newsFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.vemu.zsme.R;
import pl.vemu.zsme.detailedNews.IAsyncTaskContext;

public class NewsFragment extends Fragment implements IAsyncTaskContext {

    private NewsAdapter adapter;
    // TODO Progress bar
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recView = view.findViewById(R.id.recViewNews);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);

        progressBar = view.findViewById(R.id.progressBarNews);
        DownloadNews.setContext(this);

        setHasOptionsMenu(true);
        adapter = new NewsAdapter();
        recView.setAdapter(adapter);
        recView.addOnScrollListener(new RecScrollListener());
        new DownloadNews(1).execute(adapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(new QueryTextListener(adapter));
        searchView.setOnCloseListener(() -> {
            new DownloadNews(1, "").execute(adapter);
            return true;
        });
    }

    @Override
    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    @Override
    public void setProgressVisibility(int progressVisibility) {
        progressBar.setVisibility(progressVisibility);
    }
}
