package org.motorph.employees;

import java.util.List;

/// Common queries for employee data
public interface EmployeeRepository {
    /// Fetches all employees
    List<Employee> getAllEmployees();

    /// Fetches an employee by their Id
    Employee getEmployeeByEmployeeId(String employeeId);
}
