package pl.vemu.zsme.timetableFragment.table;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.RequiredArgsConstructor;
import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.ItemTableBinding;

@RequiredArgsConstructor
public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private final List<Lesson> day;

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Lesson lesson = day.get(position);
        holder.number.setText(lesson.getIndex());
        holder.room.setText(lesson.getRoom());
        holder.subject.setText(lesson.getName());
        holder.teacher.setText(lesson.getTeacher());
        String[] hours = lesson.getHour().replaceAll("\\s+", "").split("-");
        holder.timeStart.setText(hours[0]);
        holder.timeFinish.setText(hours[1]);
    }

    @Override
    public int getItemCount() {
        return day.size();
    }

    static class TableViewHolder extends RecyclerView.ViewHolder {

        final TextView number, subject, timeStart,
                timeFinish, room, teacher;

        TableViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemTableBinding binding = ItemTableBinding.bind(itemView);
            number = binding.number;
            subject = binding.subject;
            timeStart = binding.timeStart;
            timeFinish = binding.timeFinish;
            room = binding.room;
            teacher = binding.teacher;
        }
    }
}
