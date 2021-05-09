package pl.vemu.zsme.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import pl.vemu.zsme.BR
import pl.vemu.zsme.ui.SimpleAdapter.SimpleHolder

class SimpleAdapter<T>(private val layout: Int, var list: List<T>) : RecyclerView.Adapter<SimpleHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, layout, parent, false)
        return SimpleHolder(binding)
    }

    override fun onBindViewHolder(holder: SimpleHolder, position: Int) {
        holder.binding.setVariable(BR.model, list[position])
    }

    override fun getItemCount() = list.size

    override fun getItemId(position: Int) = position.toLong()


    class SimpleHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)
}