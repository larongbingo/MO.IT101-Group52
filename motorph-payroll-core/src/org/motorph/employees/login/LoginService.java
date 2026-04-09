package org.motorph.employees.login;

import org.motorph.core.results.Result;
import org.motorph.employees.Employee;
import org.motorph.employees.dto.NewLoginDto;

public interface LoginService {
    Result<Login> addLogin(NewLoginDto newLoginDto);
    Result<Employee> login(String username, String password);
}
