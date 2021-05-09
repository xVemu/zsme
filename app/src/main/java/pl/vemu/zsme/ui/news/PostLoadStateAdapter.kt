package pl.vemu.zsme.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.vemu.zsme.databinding.ItemPostLoadStateBinding

class PostLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<PostLoadStateAdapter.PostLoadStateViewHolder>() {

    override fun onBindViewHolder(holder: PostLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
            PostLoadStateViewHolder(ItemPostLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    inner class PostLoadStateViewHolder(
            private val binding: ItemPostLoadStateBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retry.setOnClickListener { retry() }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                retry.isVisible = loadState is LoadState.Error
                error.isVisible = loadState is LoadState.Error
            }
        }
    }
}