package org.motorph.employees;

import org.motorph.Routes;
import org.motorph.Shell;

import java.util.List;

public class ManageEmployeeListViewModel {
    private final EmployeeRepository employeeRepository;
    private final ManageEmployeesService manageEmployeesService;

    public ManageEmployeeListViewModel(EmployeeRepository employeeRepository, ManageEmployeesService manageEmployeesService) {
        this.employeeRepository = employeeRepository;
        this.manageEmployeesService = manageEmployeesService;
    }

    public List<Employee> getEmployees() {
        return employeeRepository.getAllEmployees();
    }

    public void goBackHome() {
        Shell.navigate(Routes.APP);
    }
}
