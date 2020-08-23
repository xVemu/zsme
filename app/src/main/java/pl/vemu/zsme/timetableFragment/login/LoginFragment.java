package pl.vemu.zsme.timetableFragment.login;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment implements LoginInterface {

    private LoginFragmentVM viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentLoginBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        viewModel = new ViewModelProvider(this).get(LoginFragmentVM.class);
        viewModel.setLogin(this);
        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onActivityDestroyed();
    }

    @Override
    public void login() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToTimetableFragment());
    }
}
