package org.motorph.timesheet;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;

public class Timesheet {
    public Timesheet(String employeeId, LocalDateTime startTime, LocalDateTime endTime) {
        EmployeeId = employeeId;
        StartTime = startTime;
        EndTime = endTime;
        AttendanceType = org.motorph.timesheet.AttendanceType.AtWork;
    }

    @NotNull public final String EmployeeId;
    @NotNull public final LocalDateTime StartTime;
    public LocalDateTime EndTime;
    @NotNull public final AttendanceType AttendanceType;

    public double getTotalHours() {
        return Duration.between(StartTime, EndTime).toHours();
    }
}
