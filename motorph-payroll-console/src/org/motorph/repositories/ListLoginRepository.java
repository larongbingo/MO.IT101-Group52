package org.motorph.repositories;

import org.motorph.employees.Employee;
import org.motorph.employees.EmployeeRepository;
import org.motorph.employees.Login;
import org.motorph.employees.LoginRepository;

import java.util.List;

/// In-memory implementation of LoginRepository
public class ListLoginRepository implements LoginRepository {
    private List<Login> logins = List.of();
    private EmployeeRepository employeeRepository;

    public ListLoginRepository(List<Login> logins, EmployeeRepository employeeRepository) {
        this.logins = logins;
        this.employeeRepository = employeeRepository;
    }

    /// {@inheritDoc}
    @Override
    public Employee getEmployeeByCredentials(String username, String password) {
        var login = getLoginByUsername(username);

        if (login == null)
            return null;

        if (!login.Password.equals(password))
            return null;

        return employeeRepository.getEmployeeByEmployeeId(login.EmployeeId);
    }

    /// Fetches a Login data based on the username
    private Login getLoginByUsername(String username) {
        return logins.stream().filter(x -> x.Username.equals(username)).findFirst().orElse(null);
    }
}
