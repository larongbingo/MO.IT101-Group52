package org.motorph.employees.dto;

import org.motorph.employees.Employee;
import org.motorph.employees.ManageEmployeesService;

public record UpdateEmployeeDto(String employeeId, String lastName, String firstName, String birthday, String address,
                                String phoneNumber, String sssNumber, String philHealthNumber, String taxIdNumber,
                                String pagibigMemberIdNumber, String employmentStatus, String position,
                                String basicSalary, String SupervisorId) {}