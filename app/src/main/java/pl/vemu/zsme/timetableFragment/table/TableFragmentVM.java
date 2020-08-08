package pl.vemu.zsme.timetableFragment.table;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class TableFragmentVM extends ViewModel {

    private MutableLiveData<List<List<Lesson>>> list;

    public LiveData<List<List<Lesson>>> getList() {
        return list;
    }

    public TableFragmentVM(String url) {
        list = DownloadTable.INSTANCE.getList();
        DownloadTable.INSTANCE.downloadTimetable(url);
    }

    @Override
    protected void onCleared() {
        list.setValue(new ArrayList<>());
    }
}
