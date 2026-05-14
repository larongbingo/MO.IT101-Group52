package org.motorph.data;

import org.motorph.employees.Employee;
import org.motorph.employees.login.Login;
import org.motorph.timesheet.Timesheet;

import java.util.List;

public record MotorPhData(
        List<Employee> employees,
        List<Login> logins,
        List<Timesheet> timesheets
) {}
