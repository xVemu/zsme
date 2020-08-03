package pl.vemu.zsme.moreFragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MoreAdapter<E extends Enum<E>, T extends ViewDataBinding> extends RecyclerView.Adapter<MoreAdapter.MoreHolder<T>> {

    private final int layout;
    private final Class<E> enumType;


    @NonNull
    @Override
    public MoreHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        T binding = DataBindingUtil.inflate(inflater, layout, parent, false);
        return new MoreHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreHolder holder, int position) {
        holder.binding.setVariable(BR.viewmodel, enumType.getEnumConstants()[position]);
    }

    @Override
    public int getItemCount() {
        return enumType.getEnumConstants().length;
    }

    static class MoreHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

        private T binding;

        public MoreHolder(@NonNull T binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
