package pl.vemu.zsme.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.databinding.ItemNewsBinding
import pl.vemu.zsme.util.PostComparator
import javax.inject.Inject

class PostAdapter @Inject constructor(diffCallback: PostComparator) :
    PagingDataAdapter<PostModel, PostAdapter.PostViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        PostViewHolder(ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.binding.model = getItem(position)
        //        holder.bind(null) TODO
    }

    class PostViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root)

}