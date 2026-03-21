package org.motorph.payroll;

import org.motorph.employees.Employee;
import org.motorph.timesheet.Timesheet;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PayrollService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    public double countTotalWorkHours(List<Timesheet> timesheets) {
        return timesheets.stream().map(Timesheet::getTotalHours).reduce(0.0, Double::sum);
    }

    public double calculateGrossPay(Employee employee, List<Timesheet> timesheets) {
        var totalHours = countTotalWorkHours(timesheets);
        return employee.getGrossHourlySalaryRate() * totalHours;
    }

    public double calculateTax(double income) {
        if (income < 20833.0)
            return 0; // No tax
        else if (income < 33333.0)
            return (income - 20833.0) * 0.2;
        else if (income < 66667.0)
            return 2500 + ((income - 33333.0) * 0.25);
        else if (income < 166667.0)
            return 10833 + ((income - 66667.0) * 0.3);
        else if (income < 666667.0)
            return 40833.33 + ((income - 166667.0) * 0.32);
        else
            return 200833.33 + ((income - 666667.0) * 0.35);
    }

    public double calculatePhilHealthContribution(double income) {
        if (income <= 10000) return (double) 300 / 2;
        else if (income < 60000) return Math.max(1800 / 2.0, (income * 0.03) / 2);
        else return 1800 / 2.0;
    }

    public double calculatePagIbigContribution(double income) {
        if (income < 1500) return income * 0.01;
        else return income * 0.02;
    }

    public double calculateSSSContribution(double income) {
        if (income < 3250.0) return 135.0;
        else if (income < 3750) return 157.50;
        else if (income < 4250) return 180.00;
        else if (income < 4750) return 202.50;
        else if (income < 5250) return 225.00;
        else if (income < 5750) return 247.50;
        else if (income < 6250) return 270.00;
        else if (income < 6750) return 292.50;
        else if (income < 7250) return 315.00;
        else if (income < 7750) return 337.50;
        else if (income < 8250) return 360.00;
        else if (income < 8750) return 382.50;
        else if (income < 9250) return 405.00;
        else if (income < 9750) return 427.50;
        else if (income < 10250) return 450.00;
        else if (income < 10750) return 472.50;
        else if (income < 11250) return 495.00;
        else if (income < 11750) return 517.50;
        else if (income < 12250) return 540.00;
        else if (income < 12750) return 562.50;
        else if (income < 13250) return 585.00;
        else if (income < 13750) return 607.50;
        else if (income < 14250) return 630.00;
        else if (income < 14750) return 652.50;
        else if (income < 15250) return 675.00;
        else if (income < 15750) return 697.50;
        else if (income < 16250) return 720.00;
        else if (income < 16750) return 742.50;
        else if (income < 17250) return 765.00;
        else if (income < 17750) return 787.50;
        else if (income < 18250) return 810.00;
        else if (income < 18750) return 832.50;
        else if (income < 19250) return 855.00;
        else if (income < 19750) return 877.50;
        else if (income < 20250) return 900.00;
        else if (income < 20750) return 922.50;
        else if (income < 21250) return 945.00;
        else if (income < 21750) return 967.50;
        else if (income < 22250) return 990.00;
        else if (income < 22750) return 1012.50;
        else if (income < 23250) return 1035.00;
        else if (income < 23750) return 1057.50;
        else if (income < 24250) return 1080.00;
        else if (income < 24750) return 1102.50;
        else return 1125.00;
    }
    
    public String generatePaySlip(Employee employee, List<Timesheet> timesheets) {
        var totalHours = countTotalWorkHours(timesheets);
        var grossPay = calculateGrossPay(employee, timesheets);
        var sss = calculateSSSContribution(grossPay);
        var philHealth = calculatePhilHealthContribution(grossPay);
        var pagIbig = calculatePagIbigContribution(grossPay);
        var totalDeductions = sss + philHealth + pagIbig;
        var taxableIncome = grossPay - totalDeductions;
        var tax = calculateTax(taxableIncome);
        var netPay = taxableIncome - tax;

        var firstDate = timesheets.getFirst().StartTime.format(formatter);
        var lastDate = timesheets.getLast().EndTime.format(formatter);

        var sb = new StringBuilder();
        sb.append("[MotorPH] ===" + firstDate + " - " + lastDate + "\n");
        sb.append("[MotorPH] Total Hours Worked: " + totalHours + "\n");
        sb.append("[MotorPH] Gross Pay: Php " + grossPay + "\n");
        sb.append("[MotorPH] SSS Contribution: Php " + sss + "\n");
        sb.append("[MotorPH] PhilHealth Contribution: Php " + philHealth + "\n");
        sb.append("[MotorPH] Pag-Ibig Contribution: Php " + pagIbig + "\n");
        sb.append("[MotorPH] Total Deductions: Php " + totalDeductions + "\n");
        sb.append("[MotorPH] Taxable Income: Php " + taxableIncome + "\n");
        sb.append("[MotorPH] Tax: Php " + tax + "\n");
        sb.append("[MotorPH] Net Pay: Php " + netPay + "\n");
        return sb.toString();
    }
}
