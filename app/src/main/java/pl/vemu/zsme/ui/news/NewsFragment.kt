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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pl.vemu.zsme.R
import pl.vemu.zsme.databinding.FragmentNewsBinding
import javax.inject.Inject

@AndroidEntryPoint
class NewsFragment : Fragment(), OnRefreshListener {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val args: NewsFragmentArgs by navArgs()
    private val viewModel: NewsFragmentVM by viewModels()

    @Inject
    lateinit var postAdapter: PostAdapter

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
        binding.recyclerView.adapter = postAdapter.withLoadStateHeaderAndFooter(
                header = PostLoadStateAdapter(postAdapter::retry),
                footer = PostLoadStateAdapter(postAdapter::retry),
        )
        viewLifecycleOwner.lifecycleScope.launch { //TODO remove viewLifecycleOwner?
            viewModel.posts.collectLatest {
                postAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }

    //TODO wrong color on dark mode
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)
        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = query?.let {
                binding.recyclerView.scrollToPosition(0)
                viewModel.query.value = it
                searchView.clearFocus()
                true
            } ?: false

            override fun onQueryTextChange(newText: String?): Boolean = true
        })
        searchView.setOnCloseListener {
            viewModel.query.value = ""
            searchView.onActionViewCollapsed()
            true
        }
        /*if (args.author != null) {
            searchView.onActionViewExpanded()
            searchView.setQuery(args.author, true)
        }*/
    }


    override fun onRefresh() {
        /*if (!"".contentEquals(searchView!!.query)) {
            searchView!!.setQuery(searchView!!.query, true)
        } else downloadFirstNews()*/
        viewModel.fetchPosts()
    }
}