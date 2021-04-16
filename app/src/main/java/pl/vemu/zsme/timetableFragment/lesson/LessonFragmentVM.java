package pl.vemu.zsme.timetableFragment.lesson;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LessonFragmentVM extends ViewModel {

    private final MutableLiveData<List<List<Lesson>>> list = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<List<Lesson>>> getList() {
        return list;
    }

    public LessonFragmentVM(String url) {
        new Thread(() -> {
            try {
                list.postValue(LessonRepo.INSTANCE.downloadTimetable(url));
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
