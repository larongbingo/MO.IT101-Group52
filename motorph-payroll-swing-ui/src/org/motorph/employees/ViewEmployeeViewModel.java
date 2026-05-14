package org.motorph.employees;

import org.motorph.auth.CurrentEmployeeLoggedIn;

public class ViewEmployeeViewModel {
    public Employee getEmployee() {
        return CurrentEmployeeLoggedIn.employee;
    }
}
