package org.motorph.auth;

import org.motorph.Routes;
import org.motorph.Shell;
import org.motorph.employees.Employee;
import org.motorph.employees.EmploymentStatus;

import java.time.LocalDate;

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

        // Test data
        CurrentEmployeeLoggedIn.employee = new Employee(
                "123",
                "Doe",
                "John",
                LocalDate.now(),
                "Random",
                "+09",
                "123-123-123",
                "123-123-123",
                "123-123-12300",
                "123-123-123",
                EmploymentStatus.Regular,
                "Payroll Team Lead",
                9000
        );

        Shell.navigate(Routes.APP);
    }
}
