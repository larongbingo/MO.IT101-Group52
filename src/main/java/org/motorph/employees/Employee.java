package org.motorph.employees;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Employee {
    public Employee(@NotNull String employeeId, String lastName, String firstName, LocalDate birthday, String address,
                    String phoneNumber, String sssNumber, String philHealthNumber, String taxIdNumber,
                    String pagibigMemberIdNumber, EmploymentStatus employmentStatus, String position,
                    double basicSalary) {
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
        Objects.requireNonNull(basicSalary);
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
    }

    @NotNull
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
    public UUID SupervisorId;

    public double BasicSalary;

    @NotNull
    public double getGrossSemiMonthlySalaryRate() {
        return BasicSalary / 2;
    }

    @NotNull
    public double getGrossHourlySalaryRate() {
        // (BasicSalary / 21) / 8
        return (BasicSalary / 21) / 8;
    }

    public boolean IsPayrollStaff() {
        return Position.toLowerCase().contains("payroll");
    }
}
