package pl.vemu.zsme.timetableFragment;

import java.util.List;

interface IDownloadTable {
    void makeAdapter(List<List<Lesson>> lessons);
}
