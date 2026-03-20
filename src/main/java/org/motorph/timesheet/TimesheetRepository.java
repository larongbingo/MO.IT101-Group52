package org.motorph.timesheet;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;

public interface TimesheetRepository {
    List<Timesheet> getAllTimesheetsByEmployeeIdAndDateRange(String employeeId, LocalDateTime startDate, LocalDateTime endDate);
    List<Timesheet> getAllTimesheetsByEmployeeId(String employeeId);
    List<String> getAllAvailableMonths();
    List<String> getAllAvailableMonthsByEmployeeId(String employeeId);
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
