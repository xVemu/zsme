package pl.vemu.zsme.timetableFragment.table;

import java.util.List;

public interface TableDownload {
    void makeAdapter(List<List<Lesson>> lessons);
}
