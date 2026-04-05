package org.motorph.employees;

import org.motorph.employees.dto.NewEmployeeDto;
import org.motorph.employees.dto.UpdateEmployeeDto;
import org.motorph.employees.encryption.StringEncryption;

public class ManageEmployeesService {
    private final EmployeeRepository employeeRepository;
    private final StringEncryption stringEncryption;

    public ManageEmployeesService(EmployeeRepository employeeRepository, StringEncryption stringEncryption) {
        this.employeeRepository = employeeRepository;
        this.stringEncryption = stringEncryption;
    }

    public Boolean AddEmployee(NewEmployeeDto newEmployee) {
        return false;
    }

    public Boolean UpdateEmployee(Employee employeeToUpdate, UpdateEmployeeDto updateEmployee) {
        return false;
    }

    public Boolean DeleteEmployee(Employee employeeToDelete) {
        return false;
    }
}
