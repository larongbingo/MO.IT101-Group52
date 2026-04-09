package org.motorph.repositories;

import org.motorph.core.MotorPhException;
import org.motorph.core.results.Result;
import org.motorph.employees.Employee;
import org.motorph.employees.EmployeeRepository;

import java.util.List;

/// In-memory implementation of EmployeeRepository
public class ListEmployeeRepository implements EmployeeRepository {
    private List<Employee> employees = List.of();

    public ListEmployeeRepository(List<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public Result<Employee> addEmployee(Employee newEmployee) {
        return Result.failure(new MotorPhException("Method not implemented"));
    }

    @Override
    public Result<Employee> updateEmployee(Employee updatedEmployee) {
        return Result.failure(new MotorPhException("Method not implemented"));
    }

    /// {@inheritDoc}
    @Override
    public List<Employee> getAllEmployees() {
        return employees;
    }

    /// {@inheritDoc}
    @Override
    public Employee getEmployeeByEmployeeId(String employeeId) {
        return employees.stream().filter(x -> x.EmployeeId.equals(employeeId)).findFirst().orElse(null);
    }
}
