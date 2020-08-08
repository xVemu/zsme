package pl.vemu.zsme.timetableFragment;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Setter;
import pl.vemu.zsme.BaseAdapter;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


@AllArgsConstructor
public class TableTimetableAdapter extends RecyclerView.Adapter<TableTimetableAdapter.ViewHolder> {

    private int layoutID;
    @Setter
    private List<List<Object>> list;

    @NonNull
    @Override
    public TableTimetableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView recyclerView = new RecyclerView(parent.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(parent.getContext()));
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        return new ViewHolder(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull TableTimetableAdapter.ViewHolder holder, int position) {
        holder.recyclerView.setAdapter(new BaseAdapter(layoutID, new ArrayList<>(list.get(position))));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final RecyclerView recyclerView;

        public ViewHolder(@NonNull RecyclerView itemView) {
            super(itemView);
            this.recyclerView = itemView;
        }
    }
}
