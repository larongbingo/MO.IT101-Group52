package org.motorph.employees.login;

import org.motorph.core.MotorPhException;
import org.motorph.core.results.Failure;
import org.motorph.core.results.Result;
import org.motorph.employees.Employee;
import org.motorph.employees.EmployeeRepository;
import org.motorph.employees.crypto.StringHashing;
import org.motorph.employees.dto.NewLoginDto;

public final class LoginServiceImpl implements LoginService {
    private final LoginRepository loginRepository;
    private final StringHashing stringHashing;
    private final EmployeeRepository employeeRepository;

    public LoginServiceImpl(
            LoginRepository loginRepository,
            StringHashing stringHashing,
            EmployeeRepository employeeRepository) {
        this.loginRepository = loginRepository;
        this.stringHashing = stringHashing;
        this.employeeRepository = employeeRepository;
    }

    public Result<Login> addLogin(NewLoginDto newLoginDto) {
        var hasMatchingUsername = loginRepository.getLoginByUsername(newLoginDto.Username());
        if (hasMatchingUsername != null) {
            return Result.failure(new MotorPhException("Username already taken"));
        }

        var hashedPassword = stringHashing.hash(newLoginDto.Password());

        var newLogin = new Login(newLoginDto.Employee().EmployeeId, newLoginDto.Username(), hashedPassword);

        var result = loginRepository.addLogin(newLogin);
        if (result instanceof Failure<Login>(MotorPhException exception)) {
            return Result.failure(new MotorPhException("Failed to add login", exception));
        }

        return Result.success(newLogin);
    }


    @Override
    public Result<Employee> login(String username, String password) {
        var login = loginRepository.getLoginByUsername(username);
        if (login == null) {
            return Result.failure(new MotorPhException("Invalid Username/Password"));
        }

        var matchedPasswords = stringHashing.verify(password, login.Password);
        if (!matchedPasswords) {
            return Result.failure(new MotorPhException("Invalid Username/Password"));
        }

        var employee = employeeRepository.getEmployeeByEmployeeId(login.EmployeeId);
        if (employee == null) {
            return Result.failure(new MotorPhException("Employee ID doesn't exist"));
        }

        return Result.success(employee);
    }
}
