package pl.vemu.zsme.moreFragment.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.vemu.zsme.BaseAdapter;
import pl.vemu.zsme.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class MoreFragment extends Fragment {

    private RecyclerView recyclerView;

    public MoreFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = new RecyclerView(inflater.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        return recyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView.setAdapter(new BaseAdapter(R.layout.item_more, MoreItem.values()));
        recyclerView.setHasFixedSize(true);
    }
}
