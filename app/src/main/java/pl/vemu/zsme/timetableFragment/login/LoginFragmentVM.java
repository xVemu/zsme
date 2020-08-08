package pl.vemu.zsme.timetableFragment.login;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import lombok.Setter;
import pl.vemu.zsme.R;

public class LoginFragmentVM extends AndroidViewModel implements LoginInterface {

    @Setter
    private LoginInterface login;

    private MutableLiveData<String> error = new MutableLiveData<>();
    private String wrongPasswordOrLogin;
    private String errorString;

    public LoginFragmentVM(Application context) {
        super(context);
        this.wrongPasswordOrLogin = context.getString(R.string.wrong_password_or_login);
        this.errorString = context.getString(R.string.error);
        LoginFragmentRepository.INSTANCE.setLoginInterface(this);
    }

    public void onActivityDestroyed() {
        login = null;
    }

    public void loginClick(String loginText, String passwordText) {
        LoginFragmentRepository.INSTANCE.login(loginText, passwordText);
    }

    public LiveData<String> getError() {
        return error;
    }

    @Override
    public void login() {
        new Handler(Looper.getMainLooper()).post(() -> {
            login.login();
        });
    }

    @Override
    public void reject(int code) {
        String errorText;
        if (code == 401) errorText = wrongPasswordOrLogin;
        else errorText = errorString + " " + code;
        error.postValue(errorText);
    }
}
