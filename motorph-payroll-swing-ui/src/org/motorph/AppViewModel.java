package org.motorph;

import org.motorph.auth.CurrentEmployeeLoggedIn;
import org.motorph.employees.Employee;

public class AppViewModel {
    public Employee getLoggedInEmployee() {
        return CurrentEmployeeLoggedIn.employee;
    }

    public void GoToEmployeeDetails() {
        Shell.navigate(Routes.VIEW_EMPLOYEE);
    }

    public void GoToPayroll() {
        Shell.navigate(Routes.VIEW_PAYROLL);
    }

    public void GoToManageEmployees() {
        Shell.navigate(Routes.MANAGE_EMPLOYEES);
    }
}
