package org.motorph.payroll;

import org.motorph.amper.runtime.SwingForms;
import org.motorph.listeners.AncestorListenerHandler;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class SelectTimesheetForm {
    private JPanel rootPanel;
    private JComboBox timesheetComboBox;
    private JCheckBox selectAllCheckBox;
    private JButton confirmButton;
    private JButton cancelButton;
    private SelectTimesheetViewModel viewModel;

    public SelectTimesheetForm(SelectTimesheetViewModel viewModel) {
        this.viewModel = viewModel;
        SwingForms.init(this);
        addBindings();
    }

    private void addBindings() {
        rootPanel.addAncestorListener(new AncestorListenerHandler(() -> {
            // Populate timesheets into combobox
            var timesheets = viewModel.getAllAvailableTimesheets();
            timesheetComboBox.removeAllItems();
            timesheets.forEach(timesheetComboBox::addItem);

            // Select first item
            viewModel.selectedTimesheetMonth = (String) timesheetComboBox.getSelectedItem();
        }));
        selectAllCheckBox.addItemListener(e -> {
            timesheetComboBox.setEnabled(e.getStateChange() != ItemEvent.SELECTED);
            confirmButton.setEnabled(anySelected());
        });
        timesheetComboBox.addItemListener(e -> {
            confirmButton.setEnabled(anySelected());
        });
        confirmButton.addActionListener(e -> viewModel.Submit());
        cancelButton.addActionListener(e -> viewModel.GoBack());
    }

    private boolean anySelected() {
        return selectAllCheckBox.isSelected() || timesheetComboBox.getSelectedIndex() != -1;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
