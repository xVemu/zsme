package pl.vemu.zsme.detailedNews

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import pl.vemu.zsme.R
import pl.vemu.zsme.VMFactory
import pl.vemu.zsme.databinding.FragmentDetailBinding
import pl.vemu.zsme.detailedNews.DetailFragmentDirections.ActionDetailFragmentToNewsFragment

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()
    private val viewModel: DetailFragmentVM by viewModels() {
        VMFactory(args.url)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (args.url.startsWith("author")) {
            val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            val action: ActionDetailFragmentToNewsFragment = DetailFragmentDirections.actionDetailFragmentToNewsFragment()
            action.author = args.url
            navController.navigate(action)
        }
        //        setHasOptionsMenu(true) TODO remove
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.app_bar_share -> {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, viewModel.getDetail().value.link)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, viewModel.getDetail().value.title)
            startActivity(shareIntent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }*/
}