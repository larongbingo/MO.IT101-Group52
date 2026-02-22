package org.motorph.timesheet;

import org.motorph.employees.Employee;
import org.motorph.employees.EmployeeRepository;
import org.motorph.timesheet.dto.EmployeeLeaveRequestDto;
import org.motorph.timesheet.dto.EmployeeTimeInDto;
import org.motorph.timesheet.dto.EmployeeTimeOutDto;

public class TimeClockService {
    private TimesheetRepository timesheetRepository;
    private EmployeeRepository employeeRepository;

    public TimeClockService(TimesheetRepository timesheetRepository, EmployeeRepository employeeRepository) {
        this.timesheetRepository = timesheetRepository;
        this.employeeRepository = employeeRepository;
    }

    public Boolean TimeIn(EmployeeTimeInDto dto) {
        return false;
    }

    public Boolean TimeOut(EmployeeTimeOutDto dto) {
        return false;
    }

    public Boolean RequestLeave(EmployeeLeaveRequestDto dto) {
        return false;
    }
}
