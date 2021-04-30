package pl.vemu.zsme.timetableFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.vemu.zsme.SimpleAdapter
import pl.vemu.zsme.makeSimple

class TimetableAdapter<T>(private val layoutID: Int, var list: List<List<T>>) : RecyclerView.Adapter<TimetableAdapter.TimetableHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableHolder {
        val recyclerView = RecyclerView(parent.context).makeSimple()
        return TimetableHolder(recyclerView)
    }

    override fun onBindViewHolder(holder: TimetableHolder, position: Int) {
        holder.recyclerView.adapter = SimpleAdapter(layoutID, list[position])
    }

    override fun getItemCount() = list.size


    class TimetableHolder(val recyclerView: RecyclerView) : RecyclerView.ViewHolder(recyclerView)
}