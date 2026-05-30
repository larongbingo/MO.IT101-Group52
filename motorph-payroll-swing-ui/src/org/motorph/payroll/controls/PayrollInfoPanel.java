package org.motorph.payroll.controls;

import org.motorph.payroll.Payroll;

import javax.swing.*;

public class PayrollInfoPanel {
    private JPanel rootPanel;
    private JLabel dateRangeLabel;
    private JLabel totalHoursWorkedLabel;
    private JLabel hourlyRateLabel;
    private JLabel grossPayLabel;
    private JLabel sssContributionLabel;
    private JLabel philHealthContributionLabel;
    private JLabel pagibigContributionLabel;
    private JLabel taxablePayLabel;
    private JLabel taxLabel;
    private JLabel netPayLabel;

    public PayrollInfoPanel() {
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void setPayroll(Payroll payroll) {
        dateRangeLabel.setText(payroll.dateRange().start() + " - " + payroll.dateRange().end());
        totalHoursWorkedLabel.setText(payroll.totalHours() + " hours");
        // TODO: add hourly rate into the Payroll class
        grossPayLabel.setText(String.valueOf(payroll.grossPay()));
        sssContributionLabel.setText(String.valueOf(payroll.sssContribution()));
        philHealthContributionLabel.setText(String.valueOf(payroll.philHealthContribution()));
        pagibigContributionLabel.setText(String.valueOf(payroll.pagIbigContribution()));
        taxablePayLabel.setText(String.valueOf(payroll.taxableIncome()));
        taxLabel.setText(String.valueOf(payroll.tax()));
        netPayLabel.setText(String.valueOf(payroll.netPay()));
    }
}
