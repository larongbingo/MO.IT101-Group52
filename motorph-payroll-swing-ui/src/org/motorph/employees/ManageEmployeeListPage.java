package org.motorph.employees;

import org.motorph.listeners.AncestorListenerHandler;

import javax.swing.*;
import java.util.List;


public class ManageEmployeeListPage extends JPanel {
    private final JPanel pagePanel;
    private JTable employeesTable;
    private final JScrollPane employeesTableScrollPane;
    private final ManageEmployeeListViewModel viewModel;

    public ManageEmployeeListPage(ManageEmployeeListViewModel viewModel) {
        this.viewModel = viewModel;
        employeesTableScrollPane = new JScrollPane();
        employeesTable = null;
        pagePanel = new JPanel();
        setVisible(true);
        addAncestorListener(new AncestorListenerHandler(this::addLayout));
    }

    private void addLayout() {
        pagePanel.removeAll();
        // Setup table data and build table
        var columns = new String[] { "Employee Name" };
        var data = transformToTable(viewModel.getEmployees());
        employeesTable = new JTable(data, columns);

        pagePanel.setLayout(new BoxLayout(pagePanel, BoxLayout.Y_AXIS));
        pagePanel.add(new JLabel("Manage Employee List"));
        employeesTableScrollPane.setViewportView(employeesTable);
        pagePanel.add(employeesTableScrollPane);
        add(pagePanel);

        revalidate();
        repaint();
    }

    private String[][] transformToTable(List<Employee> employees) {
        return employees.stream().map(employee -> new String[] { employee.getFullName() }).
    }
}
