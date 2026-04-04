package org.motorph.payroll;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public record DateRange(LocalDateTime start, LocalDateTime end) {
    /// Parses a Year Month String that follows the given ProcessRange
    ///
    /// if MONTH then first day of the month to last day of the month (the whole month)
    ///
    /// if FIRST_CUTOFF then first day of the month to the 15th
    ///
    /// if SECOND_CUTOFF then 16th to last day of the month
    public static DateRange parseYearMonthInRange(String month, ProcessRange rangeType) {
        var formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("MMMM yyyy")
                .toFormatter(Locale.ENGLISH);
        var date = YearMonth.parse(month.trim(), formatter);

        return switch (rangeType) {
            case MONTH -> new DateRange(date.atDay(1).atStartOfDay(), date.atEndOfMonth().atStartOfDay());
            case FIRST_CUTOFF
                    -> new DateRange(date.atDay(1).atStartOfDay(), date.atDay(15).atStartOfDay());
            case SECOND_CUTOFF
                    -> new DateRange(date.atDay(16).atStartOfDay(), date.atEndOfMonth().atStartOfDay());
        };
    }
}
