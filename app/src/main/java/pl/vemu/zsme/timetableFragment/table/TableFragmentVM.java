package pl.vemu.zsme.timetableFragment.table;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableFragmentVM extends ViewModel {

    private final MutableLiveData<List<List<Table>>> list = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<List<Table>>> getList() {
        return list;
    }

    public TableFragmentVM(String url) {
        new Thread(() -> {
            try {
                list.postValue(TableRepo.INSTANCE.downloadTimetable(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onCleared() {
        list.setValue(new ArrayList<>());
    }
}
