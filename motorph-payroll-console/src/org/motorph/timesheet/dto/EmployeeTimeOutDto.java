package org.motorph.timesheet.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeTimeOutDto(UUID EmployeeId, LocalDateTime EndDate) {}
