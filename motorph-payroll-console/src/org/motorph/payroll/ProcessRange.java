package org.motorph.payroll;

public enum ProcessRange {
    /// Whole month
    MONTH,
    /// First day of the month to 15th
    FIRST_CUTOFF,
    /// 16th to last day of the month
    SECOND_CUTOFF
}
