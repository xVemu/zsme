package pl.vemu.zsme.ui.news

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.vemu.zsme.R
import pl.vemu.zsme.SimpleAdapter
import pl.vemu.zsme.State
import pl.vemu.zsme.databinding.FragmentNewsBinding
import pl.vemu.zsme.model.PostModel

@AndroidEntryPoint
class NewsFragment : Fragment(), OnRefreshListener {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private var scrollListener: RecyclerView.OnScrollListener? = null //TODO remove
    private var searchView: SearchView? = null //TODO remove
    private val args: NewsFragmentArgs by navArgs()
    private val viewModel: NewsFragmentVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        setupRecyclerView()
        binding.refresh.apply {
            setOnRefreshListener(this@NewsFragment)
            setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
            setProgressBackgroundColorSchemeColor(resources.getColor(R.color.swipeBackground, null))
        }
        setupNetwork()
    }

    private fun setupNetwork() {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        if (cm.activeNetwork == null) Toast.makeText(context, "Brak połaczenia z internetem", Toast.LENGTH_LONG).show()
        cm.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                lifecycleScope.launch {
                    viewModel.fetchPosts()
                }
            }
        })
    }

    private fun setupRecyclerView() {
        val adapter = SimpleAdapter<PostModel>(R.layout.item_news, emptyList()).apply {
            setHasStableIds(true)
        }
        binding.recyclerView.adapter = adapter
        //        scrollListener = NewsScrollListener(viewModel, Queries.Page())
        //        binding.recyclerView.addOnScrollListener(scrollListener)
        lifecycleScope.launchWhenStarted {
            viewModel.posts.collect {
                when (it) {
                    is State.Success -> {
                        binding.refresh.isRefreshing = false
                        adapter.list = it.data
                        adapter.notifyDataSetChanged()
                    }
                    is State.Loading -> binding.refresh.isRefreshing = true
                    is State.Error -> {
                        binding.refresh.isRefreshing = false
                        Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        /*searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView!!.setOnQueryTextListener(NewsQueryTextListener(viewModel, binding.recyclerView))
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
        }*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.app_bar_search -> true
        else -> super.onOptionsItemSelected(item)
    }


    override fun onRefresh() {
        /*if (!"".contentEquals(searchView!!.query)) {
            searchView!!.setQuery(searchView!!.query, true)
        } else downloadFirstNews()*/
        viewModel.fetchPosts()
    }
}