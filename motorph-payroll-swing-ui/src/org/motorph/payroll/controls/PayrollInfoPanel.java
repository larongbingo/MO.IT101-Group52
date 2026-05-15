package org.motorph.payroll.controls;

import org.motorph.payroll.Payroll;

import javax.swing.*;

public class PayrollInfoPanel extends JPanel {
    public PayrollInfoPanel(Payroll payroll) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("Payroll Information"));
        add(new JLabel(payroll.toString()));
    }
}
