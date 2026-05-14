package org.motorph.employees;

import org.motorph.core.MotorPhException;
import org.motorph.core.results.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * ArrayList implementation of EmployeeRepository
 */
public class ListEmployeeRepository implements EmployeeRepository {
    private final ArrayList<Employee> employees = new ArrayList<>();

    @Override
    public Result<Employee> addEmployee(Employee newEmployee) {
        var result = employees.add(newEmployee);
        return result ? Result.success(newEmployee) : Result.failure(new MotorPhException("Failed to add employee"));
    }

    @Override
    public Result<Employee> updateEmployee(Employee updatedEmployee) {
        return Result.failure(new MotorPhException("Not implemented"));
    }

    @Override
    public List<Employee> getAllEmployees() {
        return List.copyOf(employees);
    }

    public Employee getEmployeeByEmployeeId(String employeeId) {
        return employees.stream().filter(e -> e.EmployeeId.equals(employeeId)).findFirst().orElse(null);
    }
}
