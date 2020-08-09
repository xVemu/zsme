package pl.vemu.zsme;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.BaseHolder> {

    private final int layout;
    @Setter
    private List<Object> list;

    public BaseAdapter(int layout, List<Object> list) {
        this.layout = layout;
        this.list = list;
    }

    public BaseAdapter(int layout, Object[] list) {
        this.layout = layout;
        this.list = Arrays.asList(list);
    }

    @NonNull
    @Override
    public BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, layout, parent, false);
        return new BaseHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder holder, int position) {
        holder.binding.setVariable(BR.viewmodel, list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class BaseHolder extends RecyclerView.ViewHolder {

        private final ViewDataBinding binding;

        public BaseHolder(@NonNull ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
