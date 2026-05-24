package org.motorph.employees;

import org.motorph.amper.runtime.SwingForms;
import org.motorph.listeners.AncestorListenerHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ManageEmployeeListForm {
    private JPanel rootPanel;
    private JButton addEmployeeButton;
    private JTable employeeTable;
    private JButton backToHomeButton;
    private JScrollPane scrollPane;
    private final ManageEmployeeListViewModel viewModel;

    public ManageEmployeeListForm(ManageEmployeeListViewModel viewModel) {
        SwingForms.init(this);
        this.viewModel = viewModel;
        backToHomeButton.addActionListener(e -> viewModel.goBackHome());
        scrollPane.setViewportView(employeeTable); // Possible bug in IntelliJ Form Designer, viewport setting can't be updated
        rootPanel.addAncestorListener(new AncestorListenerHandler(this::loadDataIntoTable));
    }

    private void loadDataIntoTable() {
        var columns = new String[] { "Full Name" };
        employeeTable.setModel(new DefaultTableModel(transformToTable(viewModel.getEmployees()), columns));
    }

    private String[][] transformToTable(List<Employee> employees) {
        return employees.stream().map(this::transformToRow).toArray(String[][]::new);
    }

    private String[] transformToRow(Employee employee) {
        return new String[] { employee.getFullName() };
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
