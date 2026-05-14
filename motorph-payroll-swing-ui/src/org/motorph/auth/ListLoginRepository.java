package org.motorph.auth;

import org.motorph.core.MotorPhException;
import org.motorph.core.results.Result;
import org.motorph.employees.login.Login;
import org.motorph.employees.login.LoginRepository;

import java.util.ArrayList;

/**
 * ArrayList implementation of LoginRepository
 */
public class ListLoginRepository implements LoginRepository {
    private final ArrayList<Login> logins = new ArrayList<>();

    @Override
    public Result<Login> addLogin(Login login) {
        var result = logins.add(login);
        return result ? Result.success(login) : Result.failure(new MotorPhException("Failed to add login"));
    }

    @Override
    public Result<Login> updateLogin(Login updatedLogin) {
        return Result.failure(new MotorPhException("Not implemented"));
    }

    @Override
    public Login getLoginByUsername(String username) {
        var login = logins.stream().filter(l -> l.Username.equals(username)).findFirst();
        return login.orElse(null);
    }
}
