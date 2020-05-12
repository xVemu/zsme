package pl.vemu.zsme.newsFragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.FragmentNewsBinding;

public class NewsFragment extends Fragment implements AsyncTaskContext {

    private NewsAdapter adapter;
    private FragmentNewsBinding binding;
    private RecyclerView.OnScrollListener scrollListener;
    private SearchView searchView;

    public NewsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(manager);

        DownloadNews.setContext(this);

        setHasOptionsMenu(true);
        adapter = new NewsAdapter();
        binding.recyclerView.setAdapter(adapter);
        scrollListener = new RecScrollListener();
        binding.recyclerView.addOnScrollListener(scrollListener);

        binding.refresh.setOnRefreshListener(() -> {
            adapter.removeAllItems();
            if (!"".contentEquals(searchView.getQuery()))
                new DownloadNews(1, searchView.getQuery().toString()).execute(adapter);
            else downloadFirstNews();
        });
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder().build();
        if (connectivityManager.getActiveNetwork() == null)
            Toast.makeText(getContext(), "Brak połaczenia z internetem", Toast.LENGTH_LONG).show();
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            getActivity().runOnUiThread(() -> downloadFirstNews());
        }
    };

    private void downloadFirstNews() {
        new DownloadNews(1).execute(adapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(new QueryTextListener(binding.recyclerView));
        searchView.setOnCloseListener(() -> {
            searchView.onActionViewCollapsed();
            binding.recyclerView.clearOnScrollListeners();
            binding.recyclerView.addOnScrollListener(scrollListener);
            adapter.removeAllItems();
            downloadFirstNews();
            return true;
        });
    }

    @Override
    public void setIsFound(boolean isFound) {
        if (binding != null) {
            binding.refresh.setRefreshing(false);
            if (isFound) {
                binding.notFound.setVisibility(View.GONE);
                binding.refresh.setVisibility(View.VISIBLE);
            } else {
                binding.notFound.setVisibility(View.VISIBLE);
                binding.refresh.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void startRefreshing() {
        binding.refresh.setRefreshing(true);
    }
}
