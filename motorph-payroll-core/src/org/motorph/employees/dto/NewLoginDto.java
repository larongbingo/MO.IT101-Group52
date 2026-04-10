package org.motorph.employees.dto;

import org.motorph.employees.Employee;

public record NewLoginDto(
        String Username,
        String Password,
        Employee Employee
) {}
