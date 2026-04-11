package org.motorph.employees.dto;

import org.motorph.core.MotorPhException;
import org.motorph.core.results.Failure;
import org.motorph.core.results.Result;
import org.motorph.core.results.Success;
import org.motorph.employees.Employee;
import org.motorph.employees.EmploymentStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public record NewEmployeeDto(
        String employeeId,
        String lastName,
        String firstName,
        String birthday,
        String address,
        String phoneNumber,
        String sssNumber,
        String philHealthNumber,
        String taxIdNumber,
        String pagibigMemberIdNumber,
        String employmentStatus,
        String position,
        String basicSalary,
        String supervisorId
) {
    public Result<Employee> toEmployee() {
        var employmentStatus = EmploymentStatus.parse(this.employmentStatus());
        if (employmentStatus instanceof Failure<EmploymentStatus>(MotorPhException exception)) {
            return Result.failure(exception);
        }

        try {
            var employee = new Employee(
                    this.employeeId(),
                    this.lastName(),
                    this.lastName(),
                    LocalDate.parse(this.birthday(), DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                    this.address(),
                    this.phoneNumber(),
                    this.sssNumber(),
                    this.philHealthNumber(),
                    this.taxIdNumber(),
                    this.pagibigMemberIdNumber(),
                    ((Success<EmploymentStatus>)employmentStatus).value(),
                    this.position(),
                    Double.parseDouble(this.basicSalary())
            );

            return Result.success(employee);
        }
        catch (NullPointerException e) {
            return Result.failure(new MotorPhException("Null value found on one of the fields", e));
        } catch (DateTimeParseException e) {
            return Result.failure(new MotorPhException("Birthday can't be parsed into date", e));
        } catch (NumberFormatException e) {
            return Result.failure(new MotorPhException("Basic Salary can't be parsed into double", e));
        }
    }
}
