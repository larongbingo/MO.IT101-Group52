package org.motorph.timesheet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TimesheetRepository {
    List<Timesheet> GetAllTimesheetsByEmployeeIdAndDateRange(UUID employeeId, LocalDateTime startDate, LocalDateTime endDate);
    Boolean AddTimesheet(Timesheet newTimesheet);
    Boolean UpdateTimesheet(Timesheet timesheetToBeUpdated);
}
