package org.motorph.payroll;

import org.motorph.employees.Employee;
import org.motorph.employees.Login;

import java.util.List;

/// Wrapper class for employee and login data
public record EmployeeLogin(List<Employee> employees, List<Login> logins) {}
