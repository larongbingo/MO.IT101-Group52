package org.motorph.timesheet;

import org.motorph.timesheet.dto.UpdateTimesheetDto;

public interface TimesheetRepository {
    Boolean AddTimesheet(Timesheet newTimesheet);
    Boolean UpdateTimesheet(Timesheet timesheetToBeUpdated, UpdateTimesheetDto dto);
}
