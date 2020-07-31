package pl.vemu.zsme.detailedNews;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
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

import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.FragmentDetailBinding;

public class DetailFragment extends Fragment implements View.OnClickListener {

    private String url;

    private FragmentDetailBinding binding;
    private DetailFragmentViewModel viewModel;

    public DetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        url = DetailFragmentArgs.fromBundle(getArguments()).getUrl();
        viewModel = new ViewModelProvider(this, new DetailFragmentViewModelFactory(requireActivity().getApplication(), url)).get(DetailFragmentViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding.text.setMovementMethod(LinkMovementMethod.getInstance());
        if (url.startsWith("author")) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            DetailFragmentDirections.ActionDetailFragmentToNewsFragment action =
                    DetailFragmentDirections.actionDetailFragmentToNewsFragment();
            action.setAuthor(url);
            navController.navigate(action);
        }
        binding.gallery.setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);
    }

    // TODO SDK 23 more options
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

    @Override
    public void onClick(View v) {
        String[] imagesArray = new String[viewModel.getImages().getValue().size()];
        viewModel.getImages().getValue().toArray(imagesArray);
        Navigation.findNavController(v).navigate(DetailFragmentDirections.actionDetailFragmentToGalleryFragment(imagesArray));
    }
}
