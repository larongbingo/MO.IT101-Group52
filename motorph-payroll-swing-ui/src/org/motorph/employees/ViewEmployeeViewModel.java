package org.motorph.employees;

import org.motorph.Routes;
import org.motorph.Shell;
import org.motorph.auth.CurrentEmployeeLoggedIn;

public class ViewEmployeeViewModel {
    public Employee getEmployee() {
        return CurrentEmployeeLoggedIn.employee;
    }

    public void GoBack() {
        Shell.navigate(Routes.APP);
    }
}
