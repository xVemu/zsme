package pl.vemu.zsme.moreFragment.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.vemu.zsme.BaseAdapter
import pl.vemu.zsme.R

//TODO deleter bcs moreFragment is same
class ContactFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        recyclerView = RecyclerView(inflater.context).apply {
            layoutManager = LinearLayoutManager(inflater.context)
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            setHasFixedSize(true)
        }
        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = BaseAdapter(R.layout.item_more, ContactItem.values())
    }
}