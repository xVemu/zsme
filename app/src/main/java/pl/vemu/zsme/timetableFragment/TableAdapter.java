package pl.vemu.zsme.timetableFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.RequiredArgsConstructor;
import pl.vemu.zsme.R;

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
        holder.timetableItemNumber.setText(lesson.getIndex());
        holder.timetableItemRoom.setText(lesson.getRoom());
        holder.timetableItemSubject.setText(lesson.getName());
        holder.timetableItemTeacher.setText(lesson.getTeacher());
        String[] hours = lesson.getHour().replaceAll("\\s+","").split("-");
        holder.timetableItemTimeStart.setText(hours[0]);
        holder.timetableItemTimeFinish.setText(hours[1]);
    }

    @Override
    public int getItemCount() {
        return day.size();
    }

    class TableViewHolder extends RecyclerView.ViewHolder {

        final TextView timetableItemNumber, timetableItemSubject, timetableItemTimeStart,
                timetableItemTimeFinish, timetableItemRoom, timetableItemTeacher;

        TableViewHolder(@NonNull View itemView) {
            super(itemView);
            this.timetableItemNumber = itemView.findViewById(R.id.timetableItemNumber);
            this.timetableItemSubject = itemView.findViewById(R.id.timetableItemSubject);
            this.timetableItemTimeStart = itemView.findViewById(R.id.timetableItemTimeStart);
            this.timetableItemTimeFinish = itemView.findViewById(R.id.timetableItemTimeFinish);
            this.timetableItemRoom = itemView.findViewById(R.id.timetableItemRoom);
            this.timetableItemTeacher = itemView.findViewById(R.id.timetableItemTeacher);
        }
    }
}
