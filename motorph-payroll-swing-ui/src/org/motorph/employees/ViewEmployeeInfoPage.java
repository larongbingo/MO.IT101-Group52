package org.motorph.employees;

import org.motorph.employees.controls.EmployeeInfoPanel;
import org.motorph.listeners.AncestorListenerHandler;

import javax.swing.*;
import java.awt.*;

public class ViewEmployeeInfoPage extends JPanel {
    private final JPanel viewEmployeeInfoPanel;
    private final JButton backButton;
    private final ViewEmployeeViewModel viewModel;
    public ViewEmployeeInfoPage(ViewEmployeeViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewEmployeeInfoPanel = new JPanel();
        this.backButton = new JButton("Back to Home");

        setVisible(true);

        addAncestorListener(new AncestorListenerHandler(this::addLayout));
        backButton.addActionListener(e -> viewModel.GoBack());
    }

    private void addLayout() {
        viewEmployeeInfoPanel.removeAll();

        Employee employee = viewModel.getEmployee();
        viewEmployeeInfoPanel.setLayout(new BorderLayout());
        viewEmployeeInfoPanel.add(new EmployeeInfoPanel(employee));
        viewEmployeeInfoPanel.add(backButton, BorderLayout.SOUTH);
        add(viewEmployeeInfoPanel);

        revalidate();
        repaint();
    }
}
