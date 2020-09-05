package pl.vemu.zsme.timetableFragment.timetable;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Objects;

import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.FragmentTimetableBinding;
import pl.vemu.zsme.timetableFragment.TableTimetableAdapter;

public class TimetableFragment extends Fragment {

    private FragmentTimetableBinding binding;
    private TimetableVM viewmodel;

    public TimetableFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTimetableBinding.inflate(inflater, container, false);
        viewmodel = new ViewModelProvider(this).get(TimetableVM.class);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder().build();
        if (connectivityManager.getActiveNetwork() == null)
            Toast.makeText(getContext(), "Brak połaczenia z internetem", Toast.LENGTH_LONG).show();
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TableTimetableAdapter adapter = new TableTimetableAdapter(R.layout.item_timetable, new ArrayList<>(Objects.requireNonNull(viewmodel.getList().getValue())));
        binding.viewPager.setAdapter(adapter);
        String[] names = {getString(R.string.classes), getString(R.string.teachers)/*, getString(R.string.classroom)*/};
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(names[position])).attach();
        viewmodel.getList().observe(getViewLifecycleOwner(), lists -> {
            adapter.setList(new ArrayList<>(lists));
            adapter.notifyDataSetChanged();
        });
        setupNetwork();
    }

    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            viewmodel.downloadTimetable();
        }
    };
}
