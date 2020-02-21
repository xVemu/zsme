package pl.vemu.zsme.timetableFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.collections4.map.LinkedMap;

import lombok.RequiredArgsConstructor;
import pl.vemu.zsme.R;

@RequiredArgsConstructor
public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.TimetableHolder> {

    private final LinkedMap<String, String> map;

    @NonNull
    @Override
    public TimetableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timetable, parent, false);
        return new TimetableHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableHolder holder, int position) {
        holder.textView.setText(map.get(position));
        TimetableFragmentDirections.ActionTimetableFragmentToTableFragment direction = TimetableFragmentDirections.actionTimetableFragmentToTableFragment(map.getValue(position));
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(direction));
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    class TimetableHolder extends RecyclerView.ViewHolder {

        final TextView textView;

        TimetableHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.cell);
        }


    }
}
