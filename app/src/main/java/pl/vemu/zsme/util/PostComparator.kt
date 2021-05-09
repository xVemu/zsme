package pl.vemu.zsme.util

import androidx.recyclerview.widget.DiffUtil
import pl.vemu.zsme.data.model.PostModel
import javax.inject.Inject

class PostComparator @Inject constructor() : DiffUtil.ItemCallback<PostModel>() {
    override fun areItemsTheSame(oldItem: PostModel, newItem: PostModel) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: PostModel, newItem: PostModel) = oldItem == newItem

}