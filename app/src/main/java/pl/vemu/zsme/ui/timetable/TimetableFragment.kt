package pl.vemu.zsme.ui.timetable

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import pl.vemu.zsme.R
import pl.vemu.zsme.State
import pl.vemu.zsme.data.model.TimetableModel
import pl.vemu.zsme.databinding.FragmentTimetableBinding

//TODO arrow back remove
@AndroidEntryPoint
class TimetableFragment : Fragment() {

    private var _binding: FragmentTimetableBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TimetableVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupNetwork() {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        if (cm.activeNetwork == null) Toast.makeText(context, "Brak połaczenia z internetem", Toast.LENGTH_LONG).show()
        cm.registerNetworkCallback(networkRequest, object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                viewModel.fetchTimetable()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = TimetableAdapter<TimetableModel>(R.layout.item_timetable, emptyList())
        lifecycleScope.launchWhenCreated {
            viewModel.list.collect {
                when (it) {
                    is State.Success -> {
                        adapter.list = it.data
                        adapter.notifyDataSetChanged()
                    }
                    is State.Error -> Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_SHORT).show()
                    else -> {
                    }
                }
            }
        }
        binding.viewPager.adapter = adapter
        val names = arrayOf(getString(R.string.classes), getString(R.string.teachers), getString(R.string.classroom))
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position -> tab.text = names[position] }.attach()
        setupNetwork()
    }
}