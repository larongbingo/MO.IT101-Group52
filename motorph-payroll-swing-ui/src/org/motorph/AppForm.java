package org.motorph;

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

        AppFormFormHelper.INSTANCE.initUI(this);

        viewOtherEmployeeButton.addActionListener(e -> viewModel.GoToManageEmployees());

        viewProfileButton.addActionListener(e -> viewModel.GoToEmployeeDetails());

        viewPayrollButton.addActionListener(e -> viewModel.GoToPayroll());
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
