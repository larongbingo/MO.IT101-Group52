package org.motorph.employees.controls;

import org.motorph.employees.Employee;
import org.motorph.listeners.TextFieldHandler;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class EmployeeInfoPanel {
    private JPanel rootPanel;
    private JTextField lastNameTextField;
    private JTextField firstNameTextField;
    private JTextField phoneNumberTextField;
    private JTextField sssTextField;
    private JTextField philhealthTextField;
    private JTextField tinTextField;
    private JTextField pagibigTextField;
    private JTextField positionTextField;
    private JComboBox employmentStatusComboBox;
    private JFormattedTextField basicSalaryTextField;
    private JLabel employeeIdLabel;
    private JFormattedTextField birthdayTextField;
    private JLabel createdAtLabel;
    private JLabel supervisorLabel;
    private Employee editableCopy;
    private Consumer<Employee> employeeConsumer;

    public EmployeeInfoPanel() {
        setIsEnabled(false);
        this.employeeConsumer =  (e) -> {};
        addBindings();
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void setEmployee(Employee employee) {
        editableCopy = new Employee(employee);
        setControls();
    }

    private void createUIComponents() {
        var doubleFormatter = new NumberFormatter(new DecimalFormat("#0.00"));
        basicSalaryTextField = new JFormattedTextField(doubleFormatter);
        var dateFormatter = new DateFormatter(new SimpleDateFormat("dd MM yyyy"));
        birthdayTextField = new JFormattedTextField(dateFormatter);
    }

    /**
     * Allows enabling edit or allow read-only mode
     */
    public void setIsEnabled(boolean isEnabled) {
        lastNameTextField.setEnabled(isEnabled);
        firstNameTextField.setEnabled(isEnabled);
        phoneNumberTextField.setEnabled(isEnabled);
        birthdayTextField.setEnabled(isEnabled);
        sssTextField.setEnabled(isEnabled);
        philhealthTextField.setEnabled(isEnabled);
        tinTextField.setEnabled(isEnabled);
        pagibigTextField.setEnabled(isEnabled);
        positionTextField.setEnabled(isEnabled);
        employmentStatusComboBox.setEnabled(isEnabled);
        basicSalaryTextField.setEnabled(isEnabled);
        birthdayTextField.setEnabled(isEnabled);
    }

    private void setControls() {
        if (editableCopy == null) {
            return;
        }

        lastNameTextField.setText(editableCopy.LastName);
        firstNameTextField.setText(editableCopy.FirstName);
        phoneNumberTextField.setText(editableCopy.PhoneNumber);
        birthdayTextField.setText(editableCopy.Birthday.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        sssTextField.setText(editableCopy.SSSNumber);
        philhealthTextField.setText(editableCopy.PhilHealthNumber);
        tinTextField.setText(editableCopy.TaxIdNumber);
        pagibigTextField.setText(editableCopy.PagibigMemberIdNumber);
        positionTextField.setText(editableCopy.Position);
        employeeIdLabel.setText(String.valueOf(editableCopy.EmployeeId));
        createdAtLabel.setText(editableCopy.CreatedAt != null ? editableCopy.CreatedAt.toString() : "");
        supervisorLabel.setText(editableCopy.SupervisorId != null ? editableCopy.SupervisorId : "N/A");
        basicSalaryTextField.setText(String.valueOf(editableCopy.BasicSalary));
        employmentStatusComboBox.setSelectedItem(editableCopy.EmploymentStatus);
    }

    private void addBindings() {
        lastNameTextField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            editableCopy.LastName = e;
            employeeConsumer.accept(editableCopy);
        }));
        firstNameTextField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            editableCopy.FirstName = e;
            employeeConsumer.accept(editableCopy);
        }));
        birthdayTextField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            //editableCopy.Birthday = LocalDate.parse(e, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            //employeeConsumer.accept(editableCopy);
        }));
        phoneNumberTextField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            editableCopy.PhoneNumber = e;
            employeeConsumer.accept(editableCopy);
        }));
        sssTextField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            editableCopy.SSSNumber = e;
            employeeConsumer.accept(editableCopy);
        }));
        philhealthTextField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            editableCopy.PhilHealthNumber = e;
            employeeConsumer.accept(editableCopy);
        }));
        tinTextField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            editableCopy.TaxIdNumber = e;
            employeeConsumer.accept(editableCopy);
        }));
        pagibigTextField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            editableCopy.PagibigMemberIdNumber = e;
            employeeConsumer.accept(editableCopy);
        }));
        positionTextField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            editableCopy.Position = e;
            employeeConsumer.accept(editableCopy);
        }));
        basicSalaryTextField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            editableCopy.BasicSalary = Double.parseDouble(e);
            employeeConsumer.accept(editableCopy);
        }));
        employmentStatusComboBox.addActionListener(e -> {
            // TODO: add logic to update employee status
        });

    }
}
