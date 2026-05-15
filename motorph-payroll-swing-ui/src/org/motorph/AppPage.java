package org.motorph;

import org.motorph.listeners.AncestorListenerHandler;

import javax.swing.*;

public class AppPage extends JPanel {
    private final JPanel appPanel;
    private final JButton viewProfileButton;
    private final JButton viewCurrentEmployeesPayrollButton;
    private final JButton viewOtherEmployeesPayrollButton;
    private final JButton logoutButton;
    private final AppViewModel viewModel;

    public AppPage(AppViewModel viewModel) {
        this.appPanel = new JPanel();
        this.viewProfileButton = new JButton("View Profile");
        this.viewCurrentEmployeesPayrollButton = new JButton("View Your Payroll");
        this.viewOtherEmployeesPayrollButton = new JButton("View Other Employees Payroll");
        this.logoutButton = new JButton("Logout");
        this.viewModel = viewModel;

        viewProfileButton.addActionListener(e -> viewModel.GoToEmployeeDetails());

        viewCurrentEmployeesPayrollButton.addActionListener(e -> viewModel.GoToPayroll());

        addAncestorListener(new AncestorListenerHandler(this::addLayout));
    }

    private void addLayout() {
        var employee = viewModel.getLoggedInEmployee();

        appPanel.setLayout(new BoxLayout(appPanel, BoxLayout.Y_AXIS));
        // FIXME: on subsequent nav back to App page, the welcome message doubles
        appPanel.add(new JLabel("Welcome " + employee.FirstName + " " + employee.LastName + " - " + employee.Position));
        appPanel.add(viewProfileButton);
        appPanel.add(viewCurrentEmployeesPayrollButton);
        if (employee.IsPayrollStaff())
            appPanel.add(viewOtherEmployeesPayrollButton);
        appPanel.add(logoutButton);
        add(appPanel);

        revalidate();
        repaint();
    }
}
