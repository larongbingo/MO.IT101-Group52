package org.motorph.employees.dto;

public record UpdateEmployeeDto(
        String LastName,
        String FirstName,
        String Birthday,
        String Address,
        String PhoneNumber,
        String SSSNumber,
        String PhilHealthNumber,
        String TaxIdNumber,
        String PagibigMemberIdNumber,
        String EmploymentStatus,
        String Position,
        String BasicSalary,
        String SupervisorId
) {}