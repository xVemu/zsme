package pl.vemu.zsme.timetableFragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;

import pl.vemu.zsme.R;
import pl.vemu.zsme.STATIC;

public class LoginFragment extends Fragment implements View.OnClickListener, IAsyncTaskContext{

    private TextView login, password, status;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        login = view.findViewById(R.id.login);
        password = view.findViewById(R.id.password);
        status = view.findViewById(R.id.status);
        view.findViewById(R.id.signIn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = login.getText().toString();
        String password = this.password.getText().toString();
        String login = username + ":" + password;

        String base64login = Base64.encodeToString(login.getBytes(), Base64.NO_WRAP);
        String url = "https://zsme.tarnow.pl/plan/lista.html";
        new DownloadTimetable(this).execute(url, base64login);
    }

    @Override
    public void login() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        STATIC.LOGGED_IN = true;
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToTimetableFragment());
    }

    @Override
    public void wrong() {
        status.setVisibility(View.VISIBLE);
    }
}
