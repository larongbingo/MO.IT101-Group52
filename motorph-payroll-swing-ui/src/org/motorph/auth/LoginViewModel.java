package org.motorph.auth;

import org.motorph.Routes;
import org.motorph.Shell;
import org.motorph.core.results.Success;
import org.motorph.employees.Employee;
import org.motorph.employees.login.LoginService;

public class LoginViewModel {
    public String username;
    public String password;
    private final LoginService loginService;

    public LoginViewModel(LoginService loginService) {
        this.loginService = loginService;
    }

    public void onLogin() {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            // TODO: Show error message
            return;
        }

        var result = loginService.login(username, password);

        if (result.isSuccess()) {
            CurrentEmployeeLoggedIn.employee = ((Success<Employee>)result).value();
            Shell.navigate(Routes.APP);
        } else {
            // TODO: Show error message
        }
    }
}
