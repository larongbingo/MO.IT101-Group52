package org.motorph.payroll.repositories;

import org.motorph.employees.Employee;
import org.motorph.employees.EmployeeRepository;

import java.util.List;

/// In-memory implementation of EmployeeRepository
public class ListEmployeeRepository implements EmployeeRepository {
    private List<Employee> employees = List.of();

    public ListEmployeeRepository(List<Employee> employees) {
        this.employees = employees;
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
