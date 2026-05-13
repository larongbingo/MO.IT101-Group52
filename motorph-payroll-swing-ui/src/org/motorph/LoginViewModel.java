package org.motorph;

public class LoginViewModel {
    public String username;
    public String password;

    public LoginViewModel() {

    }

    public void onLogin() {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            // TODO: Show error message
            return;
        }

        Shell.navigate("app");
    }
}
