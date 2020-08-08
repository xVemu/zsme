package pl.vemu.zsme.timetableFragment.table;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TableFragmentVMFactory implements ViewModelProvider.Factory {

    private final String url;

    public TableFragmentVMFactory(String url) {
        this.url = url;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TableFragmentVM.class))
            return (T) new TableFragmentVM(url);
        throw new IllegalArgumentException("ViewModel class not found");
    }
}
