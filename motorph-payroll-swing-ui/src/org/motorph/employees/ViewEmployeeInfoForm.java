package org.motorph.employees;

import org.motorph.employees.controls.EmployeeInfoPanel;
import org.motorph.listeners.AncestorListenerHandler;

import javax.swing.*;

public class ViewEmployeeInfoForm {
    private JPanel rootPanel;
    private EmployeeInfoPanel employeeInfoPanel;
    private JButton homeButton;

    public ViewEmployeeInfoForm(ViewEmployeeViewModel viewModel) {
        employeeInfoPanel.setIsEnabled(false);
        homeButton.addActionListener(e -> viewModel.GoBack());

        rootPanel.addAncestorListener(new AncestorListenerHandler(() -> {
            employeeInfoPanel.setEmployee(viewModel.getEmployee());
        }));
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
