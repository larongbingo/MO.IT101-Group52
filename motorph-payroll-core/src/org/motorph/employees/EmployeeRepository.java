package org.motorph.employees;

import org.motorph.core.results.Result;

import java.util.List;

/// Common queries for employee data
public interface EmployeeRepository {
    Result<Employee> addEmployee(Employee newEmployee);

    /// Fetches all employees
    List<Employee> getAllEmployees();

    /// Fetches an employee by their Id
    Employee getEmployeeByEmployeeId(String employeeId);
}
