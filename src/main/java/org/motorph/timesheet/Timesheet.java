package org.motorph.timesheet;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class Timesheet {
    @NotNull public final UUID EmployeeId;
    @NotNull public final LocalDateTime StartTime;
    public LocalDateTime EndTime;
    @NotNull public final AttendanceType AttendanceType;

    public Timesheet(UUID employeeId, LocalDateTime startTime, AttendanceType attendanceType) {
        EmployeeId = employeeId;
        StartTime = startTime;
        AttendanceType = attendanceType;
    }
}
