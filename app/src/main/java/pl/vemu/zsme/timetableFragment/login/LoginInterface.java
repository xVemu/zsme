package pl.vemu.zsme.timetableFragment.login;

public interface LoginInterface {
    void login();

    default void reject(int code) {
    }
}
