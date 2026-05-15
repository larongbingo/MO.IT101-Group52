package org.motorph.auth;

import org.motorph.Routes;
import org.motorph.Shell;
import org.motorph.core.MotorPhException;
import org.motorph.core.results.Result;
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

    public Result<Employee> onLogin() {
        if (username == null || username.isBlank()) {
            return Result.failure(new MotorPhException("Username cannot be empty"));
        }

        if (password == null || password.isBlank()) {
            return Result.failure(new MotorPhException("Password cannot be empty"));
        }

        var result = loginService.login(username, password);

        if (result.isSuccess()) {
            CurrentEmployeeLoggedIn.employee = ((Success<Employee>)result).value();
            Shell.navigate(Routes.APP);
            return Result.success(CurrentEmployeeLoggedIn.employee);
        } else {
            return result;
        }
    }
}
