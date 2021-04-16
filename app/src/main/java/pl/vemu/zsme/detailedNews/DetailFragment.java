package pl.vemu.zsme.detailedNews;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.FragmentDetailBinding;

public class DetailFragment extends Fragment {

    private String url;

    private FragmentDetailBinding binding;

    public DetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        url = DetailFragmentArgs.fromBundle(requireArguments()).getUrl();
        DetailFragmentVM viewModel = new ViewModelProvider(this, new DetailFragmentVMFactory(requireActivity().getApplication(), url)).get(DetailFragmentVM.class);
        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (url.startsWith("author")) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            DetailFragmentDirections.ActionDetailFragmentToNewsFragment action =
                    DetailFragmentDirections.actionDetailFragmentToNewsFragment();
            action.setAuthor(url);
            navController.navigate(action);
        }
        setHasOptionsMenu(true);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            int nightModeFlags =
                    requireContext().getResources().getConfiguration().uiMode &
                            Configuration.UI_MODE_NIGHT_MASK;
            int theme = WebSettingsCompat.FORCE_DARK_ON;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    theme = WebSettingsCompat.FORCE_DARK_ON;
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    theme = WebSettingsCompat.FORCE_DARK_OFF;
                    break;
            }
            WebSettingsCompat.setForceDark(binding.webView.getSettings(), theme);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_share) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, url);
            intent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(intent, null);
            startActivity(shareIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
