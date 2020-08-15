package pl.vemu.zsme.timetableFragment.login;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jsoup.HttpStatusException;

import java.io.IOException;

import lombok.Setter;
import pl.vemu.zsme.R;

public class LoginFragmentVM extends AndroidViewModel {

    @Setter
    private LoginInterface login;

    private final MutableLiveData<String> error = new MutableLiveData<>();
    private String wrongPasswordOrLogin;
    private String errorString;

    public LoginFragmentVM(Application context) {
        super(context);
        this.wrongPasswordOrLogin = context.getString(R.string.wrong_password_or_login);
        this.errorString = context.getString(R.string.error);
    }

    public void onActivityDestroyed() {
        login = null;
    }

    public void loginClick(String loginText, String passwordText) {
        new Thread(() -> {
            try {
                LoginRepo.INSTANCE.login(loginText, passwordText);
                new Handler(Looper.getMainLooper()).post(() -> login.login());
            } catch (HttpStatusException e) {
                reject(e.getStatusCode());
            } catch (IOException e) {
                reject(404);
            }
        }).start();
    }

    public LiveData<String> getError() {
        return error;
    }

    private void reject(int code) {
        String errorText;
        if (code == 401) errorText = wrongPasswordOrLogin;
        else errorText = errorString + " " + code;
        error.postValue(errorText);
    }
}
