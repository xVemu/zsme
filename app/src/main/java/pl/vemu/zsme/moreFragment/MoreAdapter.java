package pl.vemu.zsme.moreFragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.ItemMoreBinding;

public class MoreAdapter extends RecyclerView.Adapter<MoreAdapter.MoreHolder> {

    @NonNull
    @Override
    public MoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemMoreBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_more, parent, false);
        return new MoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreHolder holder, int position) {
        holder.binding.setViewmodel(MoreItem.values()[position]);
    }

    @Override
    public int getItemCount() {
        return MoreItem.values().length;
    }

    static class MoreHolder extends RecyclerView.ViewHolder {

        private ItemMoreBinding binding;

        public MoreHolder(@NonNull ItemMoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
