package org.motorph.employees.dto;

public record NewEmployeeDto(String employeeId, String lastName, String firstName, String birthday, String address,
                             String phoneNumber, String sssNumber, String philHealthNumber, String taxIdNumber,
                             String pagibigMemberIdNumber, String employmentStatus, String position,
                             String basicSalary) {}
