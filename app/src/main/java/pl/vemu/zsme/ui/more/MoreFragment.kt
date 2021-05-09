package pl.vemu.zsme.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import pl.vemu.zsme.R
import pl.vemu.zsme.makeSimple
import pl.vemu.zsme.ui.SimpleAdapter

//TODO arrow back remove
class MoreFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        recyclerView = RecyclerView(inflater.context).makeSimple()
        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = SimpleAdapter(R.layout.item_more, MoreItem.values().toList())
    }
}