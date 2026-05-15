package org.motorph.payroll;

import org.motorph.listeners.AncestorListenerHandler;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class SelectTimesheetPage extends JPanel {
    private final SelectTimesheetViewModel viewModel;
    private final JPanel selectTimesheetPanel;
    private final JComboBox<String> timesheetDropdown;
    private final JCheckBox selectAllCheckBox;
    private final JButton submitButton;
    private final JButton backButton;

    public SelectTimesheetPage(SelectTimesheetViewModel viewModel) {
        this.viewModel = viewModel;
        this.selectTimesheetPanel = new JPanel();
        this.timesheetDropdown = new JComboBox<>();
        this.selectAllCheckBox = new JCheckBox();
        this.submitButton = new JButton("Submit");
        this.backButton = new JButton("Cancel");

        addAncestorListener(new AncestorListenerHandler(this::addLayout));
        addBindings();
    }

    private void addLayout() {
        // Populate timesheet dropdown
        var timesheets = viewModel.getAllAvailableTimesheets().toArray(new String[0]);
        timesheetDropdown.setModel(new DefaultComboBoxModel<>(timesheets));

        // Select first item
        viewModel.selectedTimesheetMonth = timesheets[0];

        selectTimesheetPanel.setLayout(new BoxLayout(selectTimesheetPanel, BoxLayout.Y_AXIS));
        selectTimesheetPanel.add(new JLabel("Select Timesheet: "));
        selectTimesheetPanel.add(timesheetDropdown);
        selectTimesheetPanel.add(new JLabel("Select All Timesheets: "));
        selectTimesheetPanel.add(selectAllCheckBox);
        selectTimesheetPanel.add(submitButton);
        selectTimesheetPanel.add(backButton);
        add(selectTimesheetPanel);

        revalidate();
        repaint();
    }

    private void addBindings() {
        submitButton.addActionListener(e -> viewModel.Submit());

        backButton.addActionListener(e -> viewModel.GoBack());

        timesheetDropdown.addItemListener(e -> {
            viewModel.selectedTimesheetMonth = (String) timesheetDropdown.getSelectedItem();
        });

        selectAllCheckBox.addItemListener(e -> {
            timesheetDropdown.setEnabled(e.getStateChange() != ItemEvent.SELECTED);
            viewModel.isAllSelected = e.getStateChange() == ItemEvent.SELECTED;
        });
    }
}
