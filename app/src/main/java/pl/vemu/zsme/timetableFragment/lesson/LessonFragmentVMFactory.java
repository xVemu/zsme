package pl.vemu.zsme.timetableFragment.lesson;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LessonFragmentVMFactory implements ViewModelProvider.Factory {

    private final String url;

    public LessonFragmentVMFactory(String url) {
        this.url = url;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LessonFragmentVM.class))
            return (T) new LessonFragmentVM(url);
        throw new IllegalArgumentException("ViewModel class not found");
    }
}
