package org.motorph.payroll;

import org.motorph.employees.Employee;
import org.motorph.timesheet.Timesheet;

import java.util.List;

public class PayrollService {
    public double countTotalWorkHours(List<Timesheet> timesheets) {
        double totalHours = 0.0;
        for (Timesheet timesheet : timesheets) {
            totalHours += timesheet.getTotalHours();
        }
        return totalHours;
    }

    public double calculateGrossPay(Employee employee, List<Timesheet> timesheets) {
        var totalHours = countTotalWorkHours(timesheets);
        return employee.getGrossHourlySalaryRate() * totalHours;
    }
}
