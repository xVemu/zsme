package pl.vemu.zsme.newsFragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint
import pl.vemu.zsme.R
import pl.vemu.zsme.SimpleAdapter
import pl.vemu.zsme.databinding.FragmentNewsBinding
import pl.vemu.zsme.model.Post

@AndroidEntryPoint
class NewsFragment : Fragment(), OnRefreshListener {
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private var scrollListener: RecyclerView.OnScrollListener? = null //TODO remove
    private var searchView: SearchView? = null //TODO remove
    private val args: NewsFragmentArgs by navArgs()
    private val viewmodel: NewsFragmentVM by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_news, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        //        setHasOptionsMenu(true) TODO remvoe
        /*binding.refresh.apply {
            setOnRefreshListener(this@NewsFragment)
            setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
            setProgressBackgroundColorSchemeColor(resources.getColor(R.color.swipeBackground, null))
        }*/
        //        setupNetwork()
    }

    /*private fun setupNetwork() {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        if (cm.activeNetwork == null) Toast.makeText(context, "Brak połaczenia z internetem", Toast.LENGTH_LONG).show()
        cm.registerNetworkCallback(networkRequest, networkCallback)
    }*/

    private fun setupRecyclerView() {
        val adapter = SimpleAdapter<Post>(R.layout.item_news, emptyArray()).apply {
            setHasStableIds(true)
        }
        binding.recyclerView.adapter = adapter
        //        scrollListener = NewsScrollListener(viewmodel, Queries.Page())
        //        binding.recyclerView.addOnScrollListener(scrollListener)
        viewmodel.list.observe(viewLifecycleOwner) { list ->
            adapter.list = list
            adapter.notifyDataSetChanged()
        }
    }

    /*private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            if (args.author == null && viewmodel.getList().value != null && viewmodel.getList().value!!.isEmpty()) downloadFirstNews()
        }
    }*/

    /*private fun downloadFirstNews() {
        viewmodel.clearList()
        viewmodel.downloadNews(Queries.Page())
    }*/

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView!!.setOnQueryTextListener(NewsQueryTextListener(viewmodel, binding.recyclerView))
        searchView!!.setOnCloseListener {
            searchView!!.onActionViewCollapsed()
            binding.recyclerView.clearOnScrollListeners()
            binding.recyclerView.addOnScrollListener(scrollListener!!)
            //            downloadFirstNews()
            true
        }
        if (args.author != null) {
            searchView!!.onActionViewExpanded()
            searchView!!.setQuery(args.author, true)
        }
    }

    override fun onRefresh() {
        /*if (!"".contentEquals(searchView!!.query)) {
            searchView!!.setQuery(searchView!!.query, true)
        } else downloadFirstNews()*/
    }
}