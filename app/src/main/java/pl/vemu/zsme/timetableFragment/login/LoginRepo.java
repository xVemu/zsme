package pl.vemu.zsme.timetableFragment.login;

import android.util.Base64;

import java.io.IOException;

import pl.vemu.zsme.timetableFragment.Login;

public enum LoginRepo {
    INSTANCE;

    public void login(String login, String password) throws IOException {
        String credentials = login + ":" + password;
        String base64login = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        Login.INSTANCE.setBase64login(base64login);
        Login.INSTANCE.login("");
    }

}
