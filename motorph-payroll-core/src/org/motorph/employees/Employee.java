package org.motorph.employees;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Employee {
    /// Constructor for Domain Object
    public Employee(String employeeId, String lastName, String firstName, LocalDate birthday, String address,
                    String phoneNumber, String sssNumber, String philHealthNumber, String taxIdNumber,
                    String pagibigMemberIdNumber, EmploymentStatus employmentStatus, String position,
                    double basicSalary) {
        this(
                employeeId,
                lastName,
                firstName,
                birthday,
                address,
                phoneNumber,
                sssNumber,
                philHealthNumber,
                taxIdNumber,
                pagibigMemberIdNumber,
                employmentStatus,
                position,
                basicSalary,
                null,
                LocalDateTime.now(),
                null
        );
    }

    /// Constructor for ORM
    public Employee(String employeeId, String lastName, String firstName, LocalDate birthday, String address,
                    String phoneNumber, String sssNumber, String philHealthNumber, String taxIdNumber,
                    String pagibigMemberIdNumber, EmploymentStatus employmentStatus, String position,
                    double basicSalary, String supervisorId, LocalDateTime createdAt, LocalDateTime deletedAt) {
        Objects.requireNonNull(employeeId);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(birthday);
        Objects.requireNonNull(address);
        Objects.requireNonNull(phoneNumber);
        Objects.requireNonNull(sssNumber);
        Objects.requireNonNull(philHealthNumber);
        Objects.requireNonNull(taxIdNumber);
        Objects.requireNonNull(pagibigMemberIdNumber);
        Objects.requireNonNull(employmentStatus);
        Objects.requireNonNull(position);
        Objects.requireNonNull(createdAt);
        this.EmployeeId = employeeId;
        this.LastName = lastName;
        this.FirstName = firstName;
        this.Birthday = birthday;
        this.Address = address;
        this.PhoneNumber = phoneNumber;
        this.SSSNumber = sssNumber;
        this.PhilHealthNumber = philHealthNumber;
        this.TaxIdNumber = taxIdNumber;
        this.PagibigMemberIdNumber = pagibigMemberIdNumber;
        this.EmploymentStatus = employmentStatus;
        this.Position = position;
        this.BasicSalary = basicSalary;
        this.SupervisorId = supervisorId;
        this.CreatedAt = createdAt;
        this.DeletedAt = deletedAt;
    }

    public final String EmployeeId;
    public String LastName;
    public String FirstName;
    public LocalDate Birthday;
    public String Address;
    public String PhoneNumber;

    public String SSSNumber;
    public String PhilHealthNumber;
    public String TaxIdNumber;
    public String PagibigMemberIdNumber;

    public EmploymentStatus EmploymentStatus;
    public String Position;
    public String SupervisorId;

    public double BasicSalary;

    public LocalDateTime DeletedAt;
    public final LocalDateTime CreatedAt;

    public String getFullName() {
        return FirstName + " " + LastName;
    }

    public String getBasicDetails() {
        return EmployeeId + "," + getFullName() + "," + Position;
    }

    public double getGrossSemiMonthlySalaryRate() {
        return BasicSalary / 2;
    }

    public double getGrossHourlySalaryRate() {
        // (BasicSalary / 21) / 8
        return (BasicSalary / 21) / 8;
    }

    public boolean IsPayrollStaff() {
        return Position.toLowerCase().contains("payroll");
    }
}
