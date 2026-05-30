package org.motorph;

import org.motorph.listeners.AncestorListenerHandler;

import javax.swing.*;

public class AppForm {
    private JLabel greetingLabel;
    private JButton viewProfileButton;
    private JButton viewPayrollButton;
    private JButton viewOtherEmployeeButton;
    private JButton logoutButton;
    private JPanel rootPanel;
    private AppViewModel viewModel;

    public AppForm(AppViewModel viewModel) {
        this.viewModel = viewModel;

        viewOtherEmployeeButton.addActionListener(e -> viewModel.GoToManageEmployees());
        viewProfileButton.addActionListener(e -> viewModel.GoToEmployeeDetails());
        viewPayrollButton.addActionListener(e -> viewModel.GoToPayroll());
        logoutButton.addActionListener(e -> viewModel.Logout());
        rootPanel.addAncestorListener(new AncestorListenerHandler(this::onShow));
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    private void onShow() {
        var employee = viewModel.getLoggedInEmployee();

        greetingLabel.setText("Welcome, " + employee.getFullName() + " - " + employee.Position);
        viewOtherEmployeeButton.setVisible(employee.IsPayrollStaff());
    }
}
