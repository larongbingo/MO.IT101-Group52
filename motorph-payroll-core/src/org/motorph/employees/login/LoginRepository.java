package org.motorph.employees.login;

import org.motorph.core.results.Result;
import org.motorph.employees.Employee;

/// Common queries for employee login data
public interface LoginRepository {
    Result<Login> addLogin(Login login);
    Result<Login> updateLogin(Login updatedLogin);

    /// Fetches a Login data based on the username
    Login getLoginByUsername(String username);
}
