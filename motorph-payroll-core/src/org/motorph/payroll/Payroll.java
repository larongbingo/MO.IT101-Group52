package org.motorph.payroll;

import org.motorph.employees.Employee;

public record Payroll(
    Employee employee,
    DateRange dateRange,
    double totalHours,
    double grossPay,
    double sssContribution,
    double philHealthContribution,
    double pagIbigContribution,
    double totalDeductions,
    double taxableIncome,
    double tax,
    double netPay
) { }
