package org.motorph.payroll;

import org.motorph.listeners.AncestorListenerHandler;
import org.motorph.payroll.controls.PayrollInfoPanel;

import javax.swing.*;

public class CalculatePayrollPage extends JPanel {
    private final JPanel calculatePayrollPanel;
    private final JButton homeButton;
    private final CalculatePayrollViewModel viewModel;

    public CalculatePayrollPage(CalculatePayrollViewModel viewModel) {
        this.viewModel = viewModel;
        this.calculatePayrollPanel = new JPanel();
        this.homeButton = new JButton("Go Back to Home");
        addAncestorListener(new AncestorListenerHandler(this::addLayout));
        addBindings();
    }

    private void addLayout() {
        calculatePayrollPanel.removeAll();

        var payrolls = viewModel.calculatePayroll();
        calculatePayrollPanel.setLayout(new BoxLayout(calculatePayrollPanel, BoxLayout.Y_AXIS));
        calculatePayrollPanel.add(new PayrollInfoPanel(payrolls));
        calculatePayrollPanel.add(homeButton);
        add(calculatePayrollPanel);

        revalidate();
        repaint();
    }

    private void addBindings() {
        homeButton.addActionListener(e -> viewModel.goBack());
    }
}
