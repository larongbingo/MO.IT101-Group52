package org.motorph.employees;

import java.util.List;
import java.util.UUID;

public interface EmployeeRepository {
    List<Employee> GetAllEmployees();
    Employee GetEmployeeById(UUID id);
    Employee GetEmployeeByEmployeeId(String employeeId);
}
