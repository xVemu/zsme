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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Objects;

import pl.vemu.zsme.BaseAdapter;
import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.FragmentNewsBinding;

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private BaseAdapter adapter;
    private FragmentNewsBinding binding;
    private RecyclerView.OnScrollListener scrollListener;
    private SearchView searchView;
    private String author;
    private NewsFragmentVM viewmodel;

    public NewsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_news, container, false);
        viewmodel = new ViewModelProvider(this).get(NewsFragmentVM.class);
        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewmodel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        author = NewsFragmentArgs.fromBundle(requireArguments()).getAuthor();

        setupRecyclerView();

        setHasOptionsMenu(true);

        binding.refresh.setOnRefreshListener(this);
        binding.refresh.setColorSchemeResources(R.color.colorPrimary);

        setupNetwork();
    }

    private void setupNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder().build();
        if (connectivityManager.getActiveNetwork() == null)
            Toast.makeText(getContext(), "Brak połaczenia z internetem", Toast.LENGTH_LONG).show();
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    private void setupRecyclerView() {
        adapter = new BaseAdapter(R.layout.item_news, new ArrayList<>(Objects.requireNonNull(viewmodel.getList().getValue())));
        adapter.setHasStableIds(true);
        binding.recyclerView.setAdapter(adapter);
        scrollListener = new NewsScrollListener(viewmodel, new Queries.Page());
        binding.recyclerView.addOnScrollListener(scrollListener);
        viewmodel.getList().observe(getViewLifecycleOwner(), newsItems -> {
            adapter.setList(new ArrayList<>(newsItems));
            adapter.notifyDataSetChanged();
        });
    }

    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            if (author == null && viewmodel.getList().getValue() != null && viewmodel.getList().getValue().isEmpty())
                downloadFirstNews();
        }
    };

    private void downloadFirstNews() {
        viewmodel.clearList();
        viewmodel.downloadNews(new Queries.Page());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(new NewsQueryTextListener(viewmodel, binding.recyclerView));
        searchView.setOnCloseListener(() -> {
            searchView.onActionViewCollapsed();
            binding.recyclerView.clearOnScrollListeners();
            binding.recyclerView.addOnScrollListener(scrollListener);
            downloadFirstNews();
            return true;
        });
        if (author != null) {
            searchView.onActionViewExpanded();
            searchView.setQuery(author, true);
        }
    }

    @Override
    public void onRefresh() {
        if (!"".contentEquals(searchView.getQuery())) {
            searchView.setQuery(searchView.getQuery(), true);
        } else downloadFirstNews();
    }
}
