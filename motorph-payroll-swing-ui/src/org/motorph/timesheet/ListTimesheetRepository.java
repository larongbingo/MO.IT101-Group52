package org.motorph.timesheet;

import org.motorph.core.MotorPhException;
import org.motorph.core.results.Result;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListTimesheetRepository implements TimesheetRepository {
    private final ArrayList<Timesheet> timesheets = new ArrayList<>();

    @Override
    public Result<Timesheet> addTimesheet(Timesheet timesheet) {
        return timesheets.add(timesheet)
                ? Result.success(timesheet)
                : Result.failure(new MotorPhException("Failed to add timesheet"));
    }

    @Override
    public Result<Timesheet> updateTimesheet(Timesheet updatedTimesheet) {
        return Result.failure(new MotorPhException("Not implemented"));
    }

    @Override
    public List<String> getAllAvailableMonths() {
        return timesheets.stream()
                .map(x -> x.StartTime.getMonth().toString() + " " + x.StartTime.getYear())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllAvailableMonthsByEmployeeId(String employeeId) {
        return timesheets.stream()
                .filter(x -> x.EmployeeId.equals(employeeId))
                .map(x -> x.StartTime.getMonth().toString() + " " + x.StartTime.getYear())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Timesheet> getAllTimesheetsByEmployeeId(String employeeId) {
        return timesheets.stream()
                .filter(x -> x.EmployeeId.equals(employeeId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Timesheet> getAllTimesheetsByEmployeeIdAndDateRange(
            String employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        return timesheets.stream()
                .filter(x -> x.EmployeeId.equals(employeeId) && IsTimesheetInDateRange(x, startDate, endDate))
                .collect(Collectors.toList());
    }

    private boolean IsTimesheetInDateRange(Timesheet timesheet, LocalDateTime startDate, LocalDateTime endDate) {
        return timesheet.StartTime.isAfter(startDate) && timesheet.StartTime.isBefore(endDate);
    }
}
