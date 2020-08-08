package pl.vemu.zsme.timetableFragment.login;

import android.util.Base64;

import org.jsoup.HttpStatusException;

import java.io.IOException;

import lombok.Setter;
import pl.vemu.zsme.timetableFragment.Login;

public enum LoginFragmentRepository {
    INSTANCE;

    @Setter
    private LoginInterface loginInterface;

    public void login(String login, String password) {
        new Thread(() -> {
            try {
                String credentials = login + ":" + password;
                String base64login = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                Login.INSTANCE.setBase64login(base64login);
                Login.INSTANCE.login();
                loginInterface.login();
            } catch (HttpStatusException e) {
                loginInterface.reject(e.getStatusCode());
            } catch (IOException e) {
                loginInterface.reject(404);
            }
        }).start();
    }

}
