package org.motorph;

import org.motorph.auth.CurrentEmployeeLoggedIn;
import org.motorph.employees.Employee;
import org.motorph.employees.EmploymentStatus;

import java.time.LocalDate;

public class AppViewModel {
    public Employee getLoggedInEmployee() {
        return CurrentEmployeeLoggedIn.employee;
    }

    public void GoToEmployeeDetails() {
        Shell.navigate(Routes.VIEW_EMPLOYEE);
    }
}
