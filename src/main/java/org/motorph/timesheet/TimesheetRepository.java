package org.motorph.timesheet;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;

/// Common queries for timesheet data
public interface TimesheetRepository {
    /// Fetches all timesheets for an employee within a date range
    List<Timesheet> getAllTimesheetsByEmployeeIdAndDateRange(String employeeId, LocalDateTime startDate, LocalDateTime endDate);

    /// Fetches all timesheets for an employee
    List<Timesheet> getAllTimesheetsByEmployeeId(String employeeId);

    /// Fetches all months available for timesheet data.
    /// String follows the pattern (MMMM yyyy)
    List<String> getAllAvailableMonths();

    /// Fetches all months available for a given employee
    List<String> getAllAvailableMonthsByEmployeeId(String employeeId);

    /// An abstraction for getAllTimesheetsByEmployeeIdAndDateRange.
    /// It parses the given year month string to a date range.
    default List<Timesheet> getAllTimesheetsByEmployeeIdAndMonth(String employeeId, String month) {
        var formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("MMMM yyyy")
                .toFormatter(Locale.ENGLISH);
        var date = YearMonth.parse(month.trim(), formatter);
        var firstDay = date.atDay(1).atStartOfDay();
        var lastDay = date.atEndOfMonth();
        return getAllTimesheetsByEmployeeIdAndDateRange(employeeId, firstDay, lastDay.atStartOfDay());
    }
}
