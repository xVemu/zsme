package pl.vemu.zsme.ui.detail

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import pl.vemu.zsme.DetailFragmentVMFactory
import pl.vemu.zsme.R
import pl.vemu.zsme.State
import pl.vemu.zsme.data.repo.DetailRepo
import pl.vemu.zsme.databinding.FragmentDetailBinding
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var repo: DetailRepo
    private val args: DetailFragmentArgs by navArgs()
    private val viewModel: DetailVM by viewModels() {
        DetailFragmentVMFactory(repo, args.postModel.content)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        binding.webView.settings.javaScriptEnabled = true
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            val nightModeFlags = requireContext().resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            val theme = when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES -> WebSettingsCompat.FORCE_DARK_ON
                Configuration.UI_MODE_NIGHT_NO -> WebSettingsCompat.FORCE_DARK_OFF
                else -> WebSettingsCompat.FORCE_DARK_ON
            }
            WebSettingsCompat.setForceDark(binding.webView.settings, theme)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.detail.collect {
                when (it) {
                    is State.Success -> {
                        binding.progressBarDetail.visibility = View.GONE
                        binding.webView.loadData(it.data.html, "text/html; charset=UTF-8", null)
                        it.data.images?.let { images ->
                            binding.gallery.visibility = View.VISIBLE
                            binding.gallery.setOnClickListener(
                                Navigation.createNavigateOnClickListener(
                                    DetailFragmentDirections.actionDetailFragmentToGalleryFragment(
                                        images.toTypedArray()
                                    )
                                )
                            )
                        }
                    }
                    is State.Loading -> binding.progressBarDetail.visibility = View.VISIBLE
                    is State.Error -> {
                        binding.progressBarDetail.visibility = View.GONE
                        Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.app_bar_share -> {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, args.postModel.link)
                putExtra(Intent.EXTRA_TITLE, args.postModel.title)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, "Wybierz")
            startActivity(shareIntent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}