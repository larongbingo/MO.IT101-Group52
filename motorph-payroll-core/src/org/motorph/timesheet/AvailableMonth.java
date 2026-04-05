package org.motorph.timesheet;

import java.time.LocalDate;
import java.util.List;

public record AvailableMonth(String month, LocalDate startDate, LocalDate endDate) {}
