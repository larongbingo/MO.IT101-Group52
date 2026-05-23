package org.motorph.payroll;

import org.motorph.amper.runtime.SwingForms;
import org.motorph.listeners.AncestorListenerHandler;
import org.motorph.payroll.controls.PayrollInfoPanel;

import javax.swing.*;

public class CalculatePayrollForm {
    private JPanel rootPanel;
    private JScrollPane scrollPane;
    private JButton backToHomeButton;
    private JButton backToSelectionButton;
    private final JPanel innerPanel;
    private CalculatePayrollViewModel viewModel;

    public CalculatePayrollForm(CalculatePayrollViewModel viewModel) {
        SwingForms.init(this);
        this.viewModel = viewModel;
        this.innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        scrollPane.setViewportView(innerPanel);
        rootPanel.addAncestorListener(new AncestorListenerHandler(this::onShow));
        backToHomeButton.addActionListener(e -> viewModel.goHome());
        backToSelectionButton.addActionListener(e -> viewModel.goBack());
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    private void onShow() {
        innerPanel.removeAll();

        var payrolls = viewModel.calculatePayroll();
        for (var payroll : payrolls) {
            var payrollInfoPanel = new PayrollInfoPanel();
            payrollInfoPanel.setPayroll(payroll);
            innerPanel.add(payrollInfoPanel.getRootPanel());
        }

        rootPanel.revalidate();
        rootPanel.repaint();
    }
}
