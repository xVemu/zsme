package pl.vemu.zsme.timetableFragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import pl.vemu.zsme.R;
import pl.vemu.zsme.STATIC;
import pl.vemu.zsme.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment implements View.OnClickListener, IAsyncTaskContext{

    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.signIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = binding.login.getText().toString();
        String password = binding.password.getText().toString();
        String login = username + ":" + password;

        String base64login = Base64.encodeToString(login.getBytes(), Base64.NO_WRAP);
        new LoginTimetable(this).execute(base64login);
    }

    @Override
    public void login() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        STATIC.LOGGED_IN = true;
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToTimetableFragment());
    }

    @Override
    public void wrong() {
        binding.loginLayout.setError(getString(R.string.wrong_password_or_login));
    }
}
