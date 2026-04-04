package org.motorph.payroll.repositories;

import org.jetbrains.annotations.NotNull;
import org.motorph.timesheet.AvailableMonth;
import org.motorph.timesheet.Timesheet;
import org.motorph.timesheet.TimesheetRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/// In-memory implementation of TimesheetRepository
public class ListTimesheetRepository implements TimesheetRepository {
    private List<Timesheet> timesheets = List.of();

    public ListTimesheetRepository(List<Timesheet> timesheets) {
        this.timesheets = timesheets;
    }

    /// {@inheritDoc}
    @Override
    public List<Timesheet> getAllTimesheetsByEmployeeIdAndDateRange(String employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        return timesheets.stream()
                .filter(x -> x.EmployeeId.equals(employeeId) && IsTimesheetInDateRange(x, startDate, endDate))
                .collect(Collectors.toList());
    }

    /// {@inheritDoc}
    @Override
    public List<Timesheet> getAllTimesheetsByEmployeeId(String employeeId) {
        return timesheets.stream()
                .filter(x -> x.EmployeeId.equals(employeeId))
                .collect(Collectors.toList());
    }

    /// {@inheritDoc}
    @Override
    public List<String> getAllAvailableMonths() {
        return timesheets.stream()
                .map(x -> x.StartTime.getMonth().toString() + " " + x.StartTime.getYear())
                .distinct()
                .collect(Collectors.toList());
    }

    /// {@inheritDoc}
    @Override
    public List<String> getAllAvailableMonthsByEmployeeId(String employeeId) {
        return timesheets.stream()
                .filter(x -> x.EmployeeId.equals(employeeId))
                .map(x -> x.StartTime.getMonth().toString() + " " + x.StartTime.getYear())
                .distinct()
                .collect(Collectors.toList());
    }

    /// Checks if a timesheet is within the specified date range
    private boolean IsTimesheetInDateRange(@NotNull Timesheet timesheet, LocalDateTime startDate, LocalDateTime endDate) {
        return timesheet.StartTime.isAfter(startDate) && timesheet.StartTime.isBefore(endDate);
    }
}
