package pl.vemu.zsme.detailedNews;

import android.content.Intent;
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

import me.saket.bettermovementmethod.BetterLinkMovementMethod;
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
        url = DetailFragmentArgs.fromBundle(getArguments()).getUrl();
        DetailFragmentVM viewModel = new ViewModelProvider(this).get(DetailFragmentVM.class);
        viewModel.init(url);
        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding.text.setMovementMethod(BetterLinkMovementMethod.getInstance());
        if (url.startsWith("author")) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            DetailFragmentDirections.ActionDetailFragmentToNewsFragment action =
                    DetailFragmentDirections.actionDetailFragmentToNewsFragment();
            action.setAuthor(url);
            navController.navigate(action);
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
