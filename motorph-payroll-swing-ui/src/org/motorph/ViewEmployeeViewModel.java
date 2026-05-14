package org.motorph;

import org.motorph.auth.CurrentEmployeeLoggedIn;
import org.motorph.employees.Employee;

public class ViewEmployeeViewModel {
    public Employee getEmployee() {
        return CurrentEmployeeLoggedIn.employee;
    }
}
