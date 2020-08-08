package pl.vemu.zsme.timetableFragment.timetable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class TimetableVM extends ViewModel {

    public TimetableVM() {
        list = DownloadTimetable.INSTANCE.getList();
        DownloadTimetable.INSTANCE.downloadTimetable();
    }

    private MutableLiveData<List<List<Timetable>>> list;

    public LiveData<List<List<Timetable>>> getList() {
        return list;
    }

    @Override
    protected void onCleared() {
        list.setValue(new ArrayList<>());
    }
}
