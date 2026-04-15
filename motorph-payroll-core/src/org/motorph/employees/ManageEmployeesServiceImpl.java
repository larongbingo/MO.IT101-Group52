package org.motorph.employees;

import org.motorph.core.MotorPhException;
import org.motorph.core.results.Failure;
import org.motorph.core.results.Result;
import org.motorph.core.results.Success;
import org.motorph.employees.dto.NewEmployeeDto;
import org.motorph.employees.dto.UpdateEmployeeDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class ManageEmployeesServiceImpl implements ManageEmployeesService {
    private final EmployeeRepository employeeRepository;

    public ManageEmployeesServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Result<Employee> addEmployee(NewEmployeeDto newEmployee) {
        if (newEmployee == null) {
            return Result.failure(new MotorPhException("DTO is null"));
        }

        if (newEmployee.supervisorId() == null) {
            return Result.failure(new MotorPhException("Supervisor ID is null"));
        }
        if (newEmployee.supervisorId().equals(newEmployee.employeeId())) {
            return Result.failure(new MotorPhException("Supervisor cannot be the same as employee"));
        }
        if (!newEmployee.supervisorId().equalsIgnoreCase("N/A")) {
            var supervisor = employeeRepository.getEmployeeByEmployeeId(newEmployee.supervisorId());
            if (supervisor == null) {
                return Result.failure(new MotorPhException("Supervisor ID doesn't exist"));
            }
        }

        var sameEmployeeId = employeeRepository.getEmployeeByEmployeeId(newEmployee.employeeId());
        if (sameEmployeeId != null) {
            return Result.failure(new MotorPhException("ID is already taken"));
        }

        var employeeResult = newEmployee.toEmployee();
        if (employeeResult instanceof Failure<Employee>(MotorPhException exception)) {
            return Result.failure(exception);
        }

        return employeeRepository.addEmployee(((Success<Employee>)employeeResult).value());
    }

    public Result<Employee> updateEmployee(Employee employeeToUpdate, UpdateEmployeeDto updateEmployee) {
        if (employeeToUpdate == null) {
            return Result.failure(new MotorPhException("EmployeeToUpdate is null"));
        }

        if (updateEmployee == null) {
            return Result.failure(new MotorPhException("UpdateEmployee DTO is null"));
        }

        var isUpdated = updateEmployee.updateEmployee(employeeToUpdate);
        if (!isUpdated) {
            return Result.failure(new MotorPhException("No fields to update"));
        }

        var res = employeeRepository.updateEmployee(employeeToUpdate);
        if (res instanceof Failure<Employee>(MotorPhException exception)) {
            return Result.failure(exception);
        }

        return Result.success(employeeToUpdate);
    }

    public Result<Employee> deleteEmployee(Employee employeeToDelete) {
        if (employeeToDelete == null) {
            return Result.failure(new MotorPhException("EmployeeToDelete is null"));
        }

        if (employeeToDelete.DeletedAt != null) {
            return Result.failure(new MotorPhException("Employee already deleted"));
        }

        employeeToDelete.DeletedAt = LocalDateTime.now();

        var res = employeeRepository.updateEmployee(employeeToDelete);
        if (res instanceof Failure<Employee>(MotorPhException exception)) {
            return Result.failure(new MotorPhException("Failed to update", exception));
        }

        return Result.success(employeeToDelete);
    }
}
