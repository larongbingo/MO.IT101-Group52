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

        var supervisor = employeeRepository.getEmployeeByEmployeeId(newEmployee.supervisorId());
        if (supervisor == null) {
            return Result.failure(new MotorPhException(newEmployee.supervisorId() + " is not a valid ID"));
        }

        var sameEmployeeId = employeeRepository.getEmployeeByEmployeeId(newEmployee.employeeId());
        if (sameEmployeeId == null) {
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

        if (updateEmployee.LastName() != null)
            employeeToUpdate.LastName = updateEmployee.LastName();
        if (updateEmployee.FirstName() != null)
            employeeToUpdate.FirstName = updateEmployee.FirstName();
        if (updateEmployee.Birthday() != null)
            employeeToUpdate.Birthday =
                    LocalDate.parse(updateEmployee.Birthday(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        if (updateEmployee.Address() != null)
            employeeToUpdate.Address = updateEmployee.Address();
        if (updateEmployee.PhoneNumber() != null)
            employeeToUpdate.PhoneNumber = updateEmployee.PhoneNumber();
        if (updateEmployee.SSSNumber() != null)
            employeeToUpdate.SSSNumber = updateEmployee.SSSNumber();
        if (updateEmployee.PhilHealthNumber() != null)
            employeeToUpdate.PhilHealthNumber = updateEmployee.PhilHealthNumber();
        if (updateEmployee.TaxIdNumber() != null)
            employeeToUpdate.TaxIdNumber = updateEmployee.TaxIdNumber();
        if (updateEmployee.PagibigMemberIdNumber() != null)
            employeeToUpdate.PagibigMemberIdNumber = updateEmployee.PagibigMemberIdNumber();
        if (updateEmployee.EmploymentStatus() != null) {
            var res = EmploymentStatus.parse(updateEmployee.EmploymentStatus());
            if (res instanceof Failure<EmploymentStatus>(MotorPhException exception))
                return Result.failure(exception);
            var val = ((Success<EmploymentStatus>)res).value();
            employeeToUpdate.EmploymentStatus = val;
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
