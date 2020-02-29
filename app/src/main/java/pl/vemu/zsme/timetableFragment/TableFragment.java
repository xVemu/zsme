package pl.vemu.zsme.timetableFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import pl.vemu.zsme.databinding.FragmentTableBinding;

public class TableFragment extends Fragment implements IDownloadTable{

    private FragmentTableBinding binding;

    public TableFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTableBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String url = TableFragmentArgs.fromBundle(getArguments()).getUrl();
        new DownloadTable(this).execute(url);
    }

    @Override
    public void makeAdapter(List<List<Lesson>> lessons) {
        binding.viewPager.setAdapter(new TablePageAdapter(this, lessons));
    }
}
