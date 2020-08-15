package pl.vemu.zsme.timetableFragment.timetable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimetableVM extends ViewModel {

    private final MutableLiveData<List<List<Timetable>>> list = new MutableLiveData<>(new ArrayList<>());

    public TimetableVM() {
        new Thread(() -> {
            try {
                list.postValue(TimetableRepo.INSTANCE.downloadTimetable());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public LiveData<List<List<Timetable>>> getList() {
        return list;
    }

    @Override
    protected void onCleared() {
        list.setValue(new ArrayList<>());
    }
}
