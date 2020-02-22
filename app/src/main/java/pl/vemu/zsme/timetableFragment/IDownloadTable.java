package pl.vemu.zsme.timetableFragment;

import java.util.List;

public interface IDownloadTable {
    void makeAdapter(List<List<Lesson>> lessons);
}
