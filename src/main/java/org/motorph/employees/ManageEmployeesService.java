package org.motorph.employees;

import org.jetbrains.annotations.NotNull;
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

    @NotNull
    public Boolean AddEmployee(NewEmployeeDto newEmployee) {
        return false;
    }

    @NotNull
    public Boolean UpdateEmployee(Employee employeeToUpdate, UpdateEmployeeDto updateEmployee) {
        return false;
    }

    @NotNull
    public Boolean DeleteEmployee(Employee employeeToDelete) {
        return false;
    }
}
