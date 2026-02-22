package org.motorph.timesheet.dto;

import org.motorph.timesheet.AttendanceType;

import java.util.UUID;

public record EmployeeLeaveRequestDto(UUID employeeId, AttendanceType attendanceType) {}
