package pl.vemu.zsme.ui.post

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.vemu.zsme.R
import pl.vemu.zsme.data.db.PostDAO
import pl.vemu.zsme.databinding.FragmentPostBinding
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalPagingApi
class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostVM by viewModels()

    @Inject
    lateinit var postAdapter: PostAdapter

    @Inject
    lateinit var postDao: PostDAO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
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
            setOnRefreshListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    postDao.clearAll()
                    withContext(Dispatchers.Main) {
                        postAdapter.refresh()
                        this@apply.isRefreshing = false
                    }
                }
            }
            setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
            setProgressBackgroundColorSchemeColor(resources.getColor(R.color.swipeBackground, null))
        }
        setupNetwork()
    }

    private fun setupNetwork() {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        if (cm.activeNetwork == null) Toast.makeText(
            context,
            "Brak połaczenia z internetem",
            Toast.LENGTH_LONG
        ).show()
        /*cm.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                lifecycleScope.launch {
                    viewModel.fetchPosts()
                }
            }
        })TODO*/
    }


    private fun setupRecyclerView() {
        binding.recyclerView.adapter = postAdapter.withLoadStateHeaderAndFooter(
            header = PostLoadStateAdapter(postAdapter::retry),
            footer = PostLoadStateAdapter(postAdapter::retry),
        )
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.posts.collectLatest {
                postAdapter.submitData(it)
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
                viewModel.query.value = it
                searchView.clearFocus()
                binding.recyclerView.scrollToPosition(0)
                true
            } ?: false

            override fun onQueryTextChange(newText: String?): Boolean = true
        })
        searchView.setOnCloseListener {
            viewModel.query.value = ""
            searchView.onActionViewCollapsed()
            binding.recyclerView.scrollToPosition(0)
            true
        }
    }
}