package org.motorph.timesheet;

import org.motorph.employees.Employee;

public class TimeClockService {
    private TimesheetRepository timesheetRepository;

    public TimeClockService(TimesheetRepository timesheetRepository) {
        this.timesheetRepository = timesheetRepository;
    }

    public Boolean TimeIn(Employee employeeStartingShift) {
        return false;
    }

    public Boolean TimeOut(Employee employeeEndingShift) {
        return false;
    }

    public Boolean FileLeave(Employee employeeFilingLeave, AttendanceType attendanceType) {
        return false;
    }
}
