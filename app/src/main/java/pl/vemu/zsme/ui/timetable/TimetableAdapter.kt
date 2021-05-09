package pl.vemu.zsme.ui.timetable

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.vemu.zsme.makeSimple
import pl.vemu.zsme.ui.SimpleAdapter

class TimetableAdapter<T>(private val layoutID: Int, var list: List<List<T>>) : RecyclerView.Adapter<TimetableAdapter.TimetableHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            TimetableHolder(RecyclerView(parent.context).makeSimple())

    override fun onBindViewHolder(holder: TimetableHolder, position: Int) {
        holder.recyclerView.adapter = SimpleAdapter(layoutID, list[position])
    }

    override fun getItemCount() = list.size


    class TimetableHolder(val recyclerView: RecyclerView) : RecyclerView.ViewHolder(recyclerView)
}