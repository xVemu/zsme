package pl.vemu.zsme.moreFragment.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import pl.vemu.zsme.R
import pl.vemu.zsme.SimpleAdapter
import pl.vemu.zsme.makeSimple

//TODO delete bcs moreFragment is same
class ContactFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        recyclerView = RecyclerView(inflater.context).makeSimple()
        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = SimpleAdapter(R.layout.item_more, ContactItem.values())
    }
}