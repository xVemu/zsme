package pl.vemu.zsme.timetableFragment.lesson;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.FragmentTimetableBinding;
import pl.vemu.zsme.timetableFragment.TimetableAdapter;

public class LessonFragment extends Fragment {

    private FragmentTimetableBinding binding;
    private LessonFragmentVM viewmodel;

    public LessonFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String url = LessonFragmentArgs.fromBundle(requireArguments()).getUrl();
        binding = FragmentTimetableBinding.inflate(inflater, container, false);
        viewmodel = new ViewModelProvider(this, new LessonFragmentVMFactory(url)).get(LessonFragmentVM.class);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String title = LessonFragmentArgs.fromBundle(requireArguments()).getTitle();
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        TimetableAdapter adapter = new TimetableAdapter(R.layout.item_lesson, new ArrayList<>(Objects.requireNonNull(viewmodel.getList().getValue())));
        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setTabMode(TabLayout.MODE_AUTO);
        String[] names = {getString(R.string.monday), getString(R.string.tuesday), getString(R.string.wednesday), getString(R.string.thursday), getString(R.string.friday)};
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(names[position])).attach();
        viewmodel.getList().observe(getViewLifecycleOwner(), lists -> {
            adapter.setList(new ArrayList<>(lists));
            adapter.notifyDataSetChanged();
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            day = (day == 1) || (day == 7) ? 0 : day - 2;
            binding.viewPager.setCurrentItem(day);
        });
    }
}
