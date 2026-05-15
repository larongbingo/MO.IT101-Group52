package org.motorph.payroll.controls;

import org.motorph.payroll.Payroll;

import javax.swing.*;
import java.time.format.DateTimeFormatter;

public class PayrollInfoPanel extends JPanel {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    public PayrollInfoPanel(Payroll payroll) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        var firstDate = payroll.dateRange().start().format(formatter);
        var lastDate = payroll.dateRange().end().format(formatter);

        add(new JLabel("[MotorPH] === " + firstDate + " - " + lastDate));
        add(new JLabel("[MotorPH] Total Hours Worked: " + payroll.totalHours()));
        add(new JLabel("[MotorPH] Gross Pay: Php " + payroll.grossPay()));
        add(new JLabel("[MotorPH] SSS Contribution: Php " + payroll.sssContribution()));
        add(new JLabel("[MotorPH] PhilHealth Contribution: Php " + payroll.philHealthContribution()));
        add(new JLabel("[MotorPH] Pag-Ibig Contribution: Php " + payroll.pagIbigContribution()));
        add(new JLabel("[MotorPH] Total Deductions: Php " + payroll.totalDeductions()));
        add(new JLabel("[MotorPH] Taxable Income: Php " + payroll.taxableIncome()));
        add(new JLabel("[MotorPH] Tax: Php " + payroll.tax()));
        add(new JLabel("[MotorPH] Net Pay: Php " + payroll.netPay()));
    }
}
