package org.motorph;

import org.motorph.controls.EmployeeInfoPanel;
import org.motorph.employees.Employee;
import org.motorph.listeners.AncestorListenerHandler;

import javax.swing.*;
import java.awt.*;

public class ViewEmployeeInfoPage extends JPanel {
    private final JPanel viewEmployeeInfoPanel;
    private final ViewEmployeeViewModel viewModel;
    public ViewEmployeeInfoPage(ViewEmployeeViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewEmployeeInfoPanel = new JPanel();

        setVisible(true);

        addAncestorListener(new AncestorListenerHandler(this::addLayout));
    }

    private void addLayout() {
        Employee employee = viewModel.getEmployee();
        viewEmployeeInfoPanel.setLayout(new BorderLayout());
        viewEmployeeInfoPanel.add(new EmployeeInfoPanel(employee));
        add(viewEmployeeInfoPanel);

        viewEmployeeInfoPanel.revalidate();
        viewEmployeeInfoPanel.repaint();
        revalidate();
        repaint();
    }
}
